"""Run scenarios end to end with the Claude Agent SDK.

Three Claude sessions per scenario:
- the subject: a normal Claude Code session in sample-app/, where the skills under test run;
- the tester: plays the user from the scenario file (setup steps, prompts, answers to every question);
- the judge: reads what happened and marks each checklist item.

Everything runs in a sandbox clone (scenarios/.runs/sandbox) synced from this working tree, so commits,
branches and snapshot tags made during a run never reach the real repository.

  scenarios/run.sh all
  scenarios/run.sh 03 10 04
  scenarios/run.sh skill:slice            # scenarios of a skill plus everything that builds on them
  scenarios/run.sh all --dry-run          # print the plan only
"""
import argparse
import asyncio
import datetime
import json
import os
import pathlib
import re
import shutil
import subprocess
import sys
import tempfile
import time
import warnings

from claude_agent_sdk import (
    AssistantMessage,
    CanUseToolShadowedWarning,
    ClaudeAgentOptions,
    ClaudeSDKClient,
    PermissionResultAllow,
    PermissionResultDeny,
    ResultMessage,
    TextBlock,
    ToolUseBlock,
    project_key_for_directory,
)

# Read, Glob and Grep are allowed outright on purpose; only writes and Bash go through the guard.
warnings.filterwarnings("ignore", category=CanUseToolShadowedWarning)

REPO = pathlib.Path(__file__).resolve().parents[2]
SCENARIOS = REPO / "scenarios"
RUNS = SCENARIOS / ".runs"
SANDBOX = RUNS / "sandbox"
SKILLS = sorted(p.name for p in (REPO / "skills").iterdir() if (p / "SKILL.md").exists())

MAX_TESTER_TURNS = 150
SUBJECT_TIMEOUT_S = 60 * 60
# The trigger check only needs to see which skill loads; cap sessions started with stop_on_skill.
STOP_ON_SKILL_MAX_TURNS = 6

# Commands no session may run. curl is allowed only to the Figma API, in sessions the tester opens with figma.
BLOCKED = re.compile(r"\bgit\s+(push|remote)\b|\bsudo\b|\bwget\b|\bssh\b|\bscp\b")
CURL = re.compile(r"\bcurl\b")
WRITE_TOOLS = {"Edit", "Write", "MultiEdit", "NotebookEdit"}
NO_NETWORK_TOOLS = {"WebFetch", "WebSearch"}


# ---------- scenario files ----------

class Scenario:
    def __init__(self, path):
        self.path = path
        self.id = path.name[:2]
        self.text = path.read_text()
        self.title = self.text.splitlines()[0].lstrip("# ").strip()
        setup = re.search(r"^Setup: `restore ([\w-]+)`", self.text, re.M)
        if not setup:
            sys.exit(f"{path.name}: Setup must start with `restore <snapshot>`")
        self.setup = setup.group(1)

    @property
    def dependency(self):
        """The scenario whose snapshot this one starts from, or None for the baseline."""
        if self.setup == "baseline":
            return None
        return self.setup.split("-")[0]


def load_scenarios():
    return {s.id: s for s in (Scenario(p) for p in sorted(SCENARIOS.glob("[0-9][0-9]-*.md")))}


def skill_scenarios():
    """Map each skill to the scenarios whose row in scenarios/README.md names it."""
    table = {}
    for line in (SCENARIOS / "README.md").read_text().splitlines():
        row = re.match(r"\|\s*\[(\d\d)\]\([^)]*\)\s*\|([^|]*)\|", line)
        if row:
            table[row.group(1)] = re.sub(r"\(no [^)]*\)", "", row.group(2))  # "(no slice)" is not slice
    return {skill: {n for n, text in table.items() if re.search(rf"\b{skill}\b", text)} for skill in SKILLS}


def plan(scenarios, selection):
    """Pick scenarios and order them so each runs after the one it restores."""
    chosen = set()
    by_skill = skill_scenarios()
    for item in selection:
        if item == "all":
            chosen |= set(scenarios)
        elif item.startswith("skill:"):
            skill = item.removeprefix("skill:")
            if skill not in by_skill:
                sys.exit(f"Unknown skill {skill}; known: {', '.join(SKILLS)}")
            chosen |= by_skill[skill]
            chosen |= downstream(scenarios, by_skill[skill])
        elif item.zfill(2) in scenarios:
            chosen.add(item.zfill(2))
        else:
            sys.exit(f"Unknown scenario {item}")
    order, seen = [], set()

    def visit(n):
        if n in seen:
            return
        seen.add(n)
        dep = scenarios[n].dependency
        if dep in chosen:
            visit(dep)
        order.append(n)

    for n in sorted(chosen):
        visit(n)
    return order


def downstream(scenarios, roots):
    found, frontier = set(), set(roots)
    while frontier:
        frontier = {n for n, s in scenarios.items() if s.dependency in frontier} - found
        found |= frontier
    return found


# ---------- sandbox ----------

def sh(cmd, cwd=SANDBOX, check=True):
    result = subprocess.run(cmd, cwd=cwd, shell=isinstance(cmd, str), capture_output=True, text=True)
    if check and result.returncode:
        sys.exit(f"Command failed: {cmd}\n{result.stdout}{result.stderr}")
    return result.stdout.strip()


def prepare_sandbox():
    """Clone once, then bring the sandbox to this working tree, uncommitted skill edits included."""
    RUNS.mkdir(exist_ok=True)
    if not (SANDBOX / ".git").exists():
        sh(["git", "clone", "-q", str(REPO), str(SANDBOX)], cwd=RUNS)
    sh(["git", "fetch", "-q", "--force", "origin", "HEAD", "+refs/tags/sample-baseline:refs/tags/sample-baseline"])
    sh(["git", "checkout", "-q", "-f", "-B", "main", "FETCH_HEAD"])
    sh(["git", "clean", "-fdq"])
    for folder in ("skills", "scenarios"):
        sh(["rsync", "-a", "--delete", "--exclude", ".runs", "--exclude", ".venv", "--exclude", "__pycache__",
            "--exclude", "usage.csv", f"{REPO / folder}/", f"{SANDBOX / folder}/"])
    if sh(["git", "status", "--porcelain", "--", "skills", "scenarios"]):
        sh(["git", "add", "-A", "--", "skills", "scenarios"])
        sh(["git", "-c", "user.name=scenario-runner", "-c", "user.email=runner@localhost", "commit", "-q",
            "-m", "Sync the working tree for a scenario run"])
    local = REPO / "sample-app" / "local.properties"
    if local.exists():
        shutil.copy(local, SANDBOX / "sample-app" / "local.properties")
    return sh(["git", "rev-parse", "--short", "HEAD"])


def snapshot(command, name):
    return sh(["scenarios/snapshot.sh", command, name])


def has_snapshot(name):
    ref = "sample-baseline" if name == "baseline" else f"snap/{name}"
    return bool(sh(["git", "tag", "-l", ref]))


def git_state(start_ref):
    return "\n".join([
        "$ git branch --list",
        sh(["git", "branch", "--list"]),
        "$ git log --oneline --all -8",
        sh(["git", "log", "--oneline", "--all", "-8"]),
        "$ git status --short -- sample-app",
        sh(["git", "status", "--short", "--", "sample-app"]) or "(clean)",
        f"$ git diff --stat {start_ref} -- sample-app   (tracked files changed since the scenario's start)",
        sh(["git", "diff", "--stat", start_ref, "--", "sample-app"], check=False) or "(none)",
    ])


# ---------- permissions ----------

def inside(path, roots):
    try:
        resolved = pathlib.Path(path).expanduser().resolve()
    except (OSError, RuntimeError):
        return False
    return any(resolved.is_relative_to(root) for root in roots)


def guard(allow_figma=False):
    roots = [SANDBOX.resolve(), pathlib.Path(tempfile.gettempdir()).resolve(), pathlib.Path("/private/tmp")]

    def check(name, data):
        if name in NO_NETWORK_TOOLS:
            return "No web access in scenario runs."
        if name in WRITE_TOOLS and not inside(data.get("file_path") or data.get("notebook_path", ""), roots):
            return "Writes outside the sandbox are not allowed in scenario runs."
        if name == "Bash":
            command = data.get("command", "")
            if BLOCKED.search(command):
                return "This command is not allowed in scenario runs."
            if CURL.search(command) and not (allow_figma and "api.figma.com" in command):
                return "Network commands are not allowed in scenario runs."
        return None

    return check


# ---------- log ----------

class Log:
    def __init__(self, path):
        self.path = path
        self.file = path.open("w")

    def write(self, text):
        self.file.write(text.rstrip() + "\n\n")
        self.file.flush()

    def tool(self, who, block):
        data = block.input
        if block.name == "Bash":
            detail = data.get("command", "")
        elif block.name in ("Read", "Edit", "Write", "MultiEdit"):
            detail = data.get("file_path", "")
        elif block.name == "AskUserQuestion":
            return  # logged with its answers
        else:
            detail = json.dumps(data, ensure_ascii=False)
        self.write(f"`{who}` tool **{block.name}**: `{shorten(detail, 400)}`")


def shorten(text, limit):
    text = " ".join(str(text).split())
    return text if len(text) <= limit else text[:limit] + " …"


# ---------- tester ----------

TESTER_SCHEMA = {
    "type": "object",
    "properties": {
        "action": {"type": "string", "enum": ["new_session", "send", "answer", "finish"]},
        "text": {"type": "string", "description": "Message to the subject for new_session / send."},
        "answers": {
            "type": "object",
            "additionalProperties": {"type": "string"},
            "description": "For answer: question text -> chosen option label (comma-separated for multi-select).",
        },
        "stop_on_skill": {"type": "boolean", "description": "new_session only: stop as soon as a skill loads."},
        "figma": {"type": "boolean", "description": "new_session only: keep FIGMA_TOKEN for a real Figma file."},
        "note": {"type": "string", "description": "Anything the judge should know: unscripted answers, skipped parts."},
    },
    "required": ["action"],
}

TESTER_PROMPT = """You play the user in a scripted manual test of Claude Code skills. A separate Claude Code
session (the subject) runs in {sample_app}; you never talk to it except through your JSON actions.
The sandbox repository root is {sandbox}; you may use Read, Glob, Grep, Bash, Edit and Write there.

Follow the scenario file exactly:
- Do every setup or mid-scenario step yourself with your tools, from {sandbox}: `restore X` means
  `scenarios/snapshot.sh restore X`, `save X` means `scenarios/snapshot.sh save X`; hand edits, `git restore`,
  and checks on files are yours too. The scenario's first `restore` has already been done for you.
- Send prompts verbatim. Replace `<slug>` with the feature's real folder name under
  {sample_app}/docs/features/ (look it up; the existing feature is title-discovery).
- Answer questions as the scenario says. A question it doesn't answer: pick the recommended option (the
  first one, or the one marked recommended) and add a note "unscripted: <question> -> <answer>".
- "New session" or "in a new session" means action new_session. "Stop the session (Esc)" after a skill
  loads means new_session with stop_on_skill true for that prompt.
- Never hint at what the scenario expects and never judge the subject; just act as the user would.
- Sessions start with FIGMA_TOKEN unset. Open a session with figma true only when the scenario reads a real
  Figma file. Parts that need something you don't have: skip them and say so in a note.
- When every step of the scenario is done, return action finish.

Actions (always reply with the JSON object only):
- new_session: start a fresh subject session and send `text` as its first message.
- send: send `text` to the current subject session.
- answer: only when you are shown a question from the subject; fill `answers` with one entry per
  question, keyed by the exact question text, valued by the exact option label (or your own words if
  no option fits, as a user typing "Other" would).
- finish: the scenario is over.
"""


class Tester:
    def __init__(self, scenario, log, model, figma):
        self.scenario = scenario
        self.log = log
        self.check = guard()
        figma = ("FIGMA_TOKEN is set: a new_session with figma true keeps it." if figma
                 else "FIGMA_TOKEN is not set: skip every part that needs real Figma access.")
        system = TESTER_PROMPT.format(sandbox=SANDBOX, sample_app=SANDBOX / "sample-app")
        self.first = f"Scenario file {scenario.path.name}:\n\n{scenario.text}\n\n{figma}\n\nStart: do the setup, then your first action."
        self.options = ClaudeAgentOptions(
            cwd=str(SANDBOX), system_prompt=system, model=model, setting_sources=[], skills=[],
            tools=["Read", "Glob", "Grep", "Bash", "Edit", "Write"], allowed_tools=["Read", "Glob", "Grep"],
            can_use_tool=self.permit, output_format={"type": "json_schema", "schema": TESTER_SCHEMA},
        )
        self.client = None
        self.turns = 0
        self.cost = 0.0

    async def permit(self, name, data, _context):
        reason = self.check(name, data)
        if reason:
            return PermissionResultDeny(message=reason)
        if name == "Bash":
            self.log.write(f"`tester` runs: `{shorten(data.get('command', ''), 400)}`")
        elif name in WRITE_TOOLS:
            self.log.write(f"`tester` edits: `{data.get('file_path', '')}`")
        return PermissionResultAllow()

    async def ask(self, message=None):
        self.turns += 1
        if self.turns > MAX_TESTER_TURNS:
            return {"action": "finish", "note": f"Stopped by the runner after {MAX_TESTER_TURNS} tester turns."}
        if self.client is None:
            self.client = ClaudeSDKClient(self.options)
            await self.client.connect()
            message = self.first
        await self.client.query(message)
        action, text = None, ""
        async for msg in self.client.receive_response():
            if isinstance(msg, AssistantMessage):
                text = "".join(b.text for b in msg.content if isinstance(b, TextBlock)) or text
            if isinstance(msg, ResultMessage):
                self.cost += msg.total_cost_usd or 0
                action = msg.structured_output or parse_json(msg.result or text)
        if not isinstance(action, dict) or "action" not in action:
            return {"action": "finish", "note": f"Tester gave no valid action: {shorten(text, 300)}"}
        if action.get("note"):
            self.log.write(f"`tester` note: {action['note']}")
        return action

    async def close(self):
        if self.client:
            await self.client.disconnect()


def parse_json(text):
    match = re.search(r"\{.*\}", text or "", re.S)
    try:
        return json.loads(match.group(0)) if match else None
    except json.JSONDecodeError:
        return None


# ---------- subject ----------

class Subject:
    def __init__(self, tester, log, model, figma):
        self.tester = tester
        self.log = log
        self.check = guard(allow_figma=figma)
        self.figma = figma
        self.model = model
        self.client = None
        self.sessions = []
        self.stop_on_skill = False

    async def start(self, stop_on_skill):
        await self.close()
        # An empty FIGMA_TOKEN reads as unset to `[ -n "$FIGMA_TOKEN" ]`.
        env = {} if self.figma else {"FIGMA_TOKEN": ""}
        self.check = guard(allow_figma=self.figma)
        options = ClaudeAgentOptions(
            cwd=str(SANDBOX / "sample-app"), model=self.model, setting_sources=["project"], skills=SKILLS,
            can_use_tool=self.permit, env=env,
            max_turns=STOP_ON_SKILL_MAX_TURNS if stop_on_skill else None,
        )
        self.client = ClaudeSDKClient(options)
        await self.client.connect()
        self.stop_on_skill = stop_on_skill
        self.log.write(f"## Subject session {len(self.sessions) + 1}")

    async def permit(self, name, data, _context):
        if name == "AskUserQuestion":
            return await self.answer(data)
        reason = self.check(name, data)
        if reason:
            self.log.write(f"`runner` denied **{name}**: {reason}")
            return PermissionResultDeny(message=reason)
        return PermissionResultAllow()

    async def answer(self, data):
        questions = data.get("questions", [])
        shown = json.dumps(questions, ensure_ascii=False, indent=1)
        action = await self.tester.ask(
            "The subject asks you (AskUserQuestion). Reply with action answer.\n" + shown)
        answers = action.get("answers") or {}
        lines = [f"- **{q.get('question')}** → {answers.get(q.get('question'), '(no answer)')}" for q in questions]
        self.log.write("`subject` asks; `tester` answers:\n" + "\n".join(lines))
        return PermissionResultAllow(updated_input={**data, "answers": answers})

    async def send(self, text):
        """Send one user message and run the subject's turn to its end. Returns its last text."""
        self.log.write(f"`tester` says:\n\n> " + text.replace("\n", "\n> "))
        await self.client.query(text)
        last, stopped = "", False
        started = time.monotonic()
        async for msg in self.client.receive_response():
            if isinstance(msg, AssistantMessage):
                for block in msg.content:
                    if isinstance(block, TextBlock) and block.text.strip():
                        last = block.text
                        self.log.write(f"`subject`: {block.text}")
                    elif isinstance(block, ToolUseBlock):
                        self.log.tool("subject", block)
                        if block.name == "Skill" and self.stop_on_skill and not stopped:
                            stopped = True
                            self.log.write(f"`runner` stops the session: skill `{block.input.get('skill')}` loaded.")
                            await self.client.interrupt()
                if not stopped and time.monotonic() - started > SUBJECT_TIMEOUT_S:
                    stopped = True
                    self.log.write("`runner` interrupts the session: turn ran over the time limit.")
                    await self.client.interrupt()
            elif isinstance(msg, ResultMessage):
                if msg.session_id not in self.sessions:
                    self.sessions.append(msg.session_id)
                if msg.is_error and not stopped:
                    self.log.write(f"`runner`: turn ended with an error ({msg.subtype}): {msg.errors or msg.result}")
        return last, stopped

    async def close(self):
        if self.client:
            await self.client.disconnect()
            self.client = None


# ---------- judge ----------

JUDGE_SCHEMA = {
    "type": "object",
    "properties": {
        "items": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "check": {"type": "string"},
                    "verdict": {"type": "string", "enum": ["pass", "fail", "unclear", "skipped"]},
                    "evidence": {"type": "string"},
                },
                "required": ["check", "verdict", "evidence"],
            },
        },
        "summary": {"type": "string"},
    },
    "required": ["items", "summary"],
}

JUDGE_PROMPT = """You grade one scenario run of Claude Code skills. Mark every `- [ ]` item of the scenario
file, in order, with a verdict and short evidence (a file path and what is in it, or a line of the log).

- pass: the evidence shows it. fail: the evidence shows it didn't happen or happened wrongly.
- unclear: you can't tell from the log and the files. skipped: the tester skipped that part (see its notes).
- Read the files in {sample_app} to check outputs; the log only lists tool calls, not their results.
- The log's "subject" lines are the session under test; "tester" lines are the scripted user; "runner"
  lines come from the harness. Grade the subject only.
- Be strict and literal; don't give credit for intent.
"""


async def judge(scenario, log_path, state, model):
    from claude_agent_sdk import query

    prompt = (f"Scenario file {scenario.path.name}:\n\n{scenario.text}\n\n"
              f"Run log: {log_path}\n\nGit state of the sandbox after the run:\n{state}\n")
    options = ClaudeAgentOptions(
        cwd=str(SANDBOX), model=model, setting_sources=[], skills=[], tools=["Read", "Glob", "Grep"],
        allowed_tools=["Read", "Glob", "Grep"], add_dirs=[str(log_path.parent)],
        system_prompt=JUDGE_PROMPT.format(sample_app=SANDBOX / "sample-app"),
        output_format={"type": "json_schema", "schema": JUDGE_SCHEMA},
    )
    result, cost = None, 0.0
    async for msg in query(prompt=prompt, options=options):
        if isinstance(msg, ResultMessage):
            cost = msg.total_cost_usd or 0
            result = msg.structured_output or parse_json(msg.result)
    if not isinstance(result, dict):
        result = {"items": [], "summary": "The judge returned no result."}
    return result, cost


# ---------- one scenario ----------

async def run_scenario(scenario, out, args):
    out.mkdir(parents=True, exist_ok=True)
    log = Log(out / "log.md")
    log.write(f"# {scenario.title}")
    snapshot("restore", scenario.setup)
    start_ref = "sample-baseline" if scenario.setup == "baseline" else f"snap/{scenario.setup}"
    figma = bool(os.environ.get("FIGMA_TOKEN")) and not args.no_figma
    tester = Tester(scenario, log, args.tester_model, figma)
    subject = Subject(tester, log, args.model, figma=False)
    started = time.monotonic()
    try:
        action = await tester.ask()
        while action["action"] != "finish":
            kind, text = action["action"], action.get("text", "")
            if kind == "new_session" or (kind == "send" and subject.client is None):
                subject.figma = figma and bool(action.get("figma"))
                await subject.start(bool(action.get("stop_on_skill")))
            if kind in ("new_session", "send") and text:
                last, stopped = await subject.send(text)
                ending = "The runner stopped it once a skill loaded." if stopped else "Its turn ended."
                status = sh(["git", "status", "--short", "--", "sample-app"]) or "(clean)"
                action = await tester.ask(f"{ending} Its last message:\n\n{last}\n\n"
                                          f"`git status --short -- sample-app`:\n{status}\n\nYour next action?")
            else:
                action = await tester.ask("That action did nothing (send needs text; answer only fits a "
                                          "question). Your next action?")
    finally:
        await subject.close()
        await tester.close()
    minutes = (time.monotonic() - started) / 60
    state = git_state(start_ref)
    verdict, judge_cost = await judge(scenario, log.path, state, args.judge_model)
    (out / "judge.json").write_text(json.dumps(verdict, ensure_ascii=False, indent=2))
    counts = {v: sum(1 for i in verdict["items"] if i["verdict"] == v) for v in ("pass", "fail", "unclear", "skipped")}
    passed = counts["fail"] == 0 and verdict["items"] != []
    record_usage(scenario, subject.sessions)
    return {
        "id": scenario.id, "title": scenario.title, "passed": passed, "counts": counts, "verdict": verdict,
        "sessions": subject.sessions, "minutes": minutes, "helper_cost": tester.cost + judge_cost,
    }


def record_usage(scenario, sessions):
    folder = pathlib.Path.home() / ".claude" / "projects" / project_key_for_directory(str(SANDBOX / "sample-app"))
    for session in sessions:
        transcript = folder / f"{session}.jsonl"
        if transcript.exists():
            subprocess.run([sys.executable, str(REPO / "usage.py"), "record", scenario.id,
                            "--session", str(transcript), "--note", "runner"], capture_output=True)


# ---------- report ----------

def write_report(path, results, skipped, commit, run_id):
    lines = [f"# Scenario run {run_id}", "", f"Sandbox commit `{commit}`.", "",
             "| # | Scenario | Result | pass / fail / unclear / skipped | Minutes | Tester + judge $ |",
             "| --- | --- | --- | --- | --- | --- |"]
    for r in results:
        c = r["counts"]
        result = "PASS" if r["passed"] else "FAIL"
        lines.append(f"| {r['id']} | {r['title']} | {result} | {c['pass']} / {c['fail']} / {c['unclear']} / "
                     f"{c['skipped']} | {r['minutes']:.0f} | {r['helper_cost']:.2f} |")
    for n, reason in skipped:
        lines.append(f"| {n} | | SKIPPED | {reason} | | |")
    for r in results:
        bad = [i for i in r["verdict"]["items"] if i["verdict"] != "pass"]
        lines += ["", f"## {r['id']} - {r['title']}", "", r["verdict"].get("summary", ""), "",
                  f"Log: [{r['id']}/log.md]({r['id']}/log.md)"]
        for i in bad:
            lines.append(f"- **{i['verdict']}**: {i['check']} — {i['evidence']}")
    path.write_text("\n".join(lines) + "\n")


def add_finding(scenario, result, run_id):
    """Append one dated line to the scenario's Findings in this working tree."""
    path = SCENARIOS / scenario.path.name
    bad = [i for i in result["verdict"]["items"] if i["verdict"] in ("fail", "unclear")]
    date = datetime.date.today().isoformat()
    line = f"- {date} (runner {run_id}): {'PASS' if result['passed'] else 'FAIL'}"
    if bad:
        line += "; " + "; ".join(f"{i['verdict']}: {shorten(i['check'], 80)}" for i in bad)
    text = path.read_text().rstrip("\n")
    text = re.sub(r"\n-\s*$", "", text)  # drop the empty placeholder bullet
    path.write_text(text + "\n" + line + "\n")


# ---------- main ----------

async def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("selection", nargs="+", help="all, scenario numbers, or skill:<name>")
    parser.add_argument("--model", help="model for the subject session (default: Claude Code's default)")
    parser.add_argument("--tester-model", default="sonnet")
    parser.add_argument("--judge-model", default="sonnet")
    parser.add_argument("--no-figma", action="store_true", help="hide FIGMA_TOKEN from every session (skips 18)")
    parser.add_argument("--no-findings", action="store_true", help="don't append to the scenarios' Findings")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    scenarios = load_scenarios()
    order = plan(scenarios, args.selection)
    print("Plan: " + " → ".join(order))
    if args.dry_run:
        return

    commit = prepare_sandbox()
    run_id = datetime.datetime.now().strftime("%Y%m%d-%H%M%S")
    out = RUNS / run_id
    out.mkdir(parents=True)
    results, skipped, failed = [], [], set()
    for n in order:
        scenario = scenarios[n]
        dep = scenario.dependency
        if dep in failed:
            skipped.append((n, f"{dep} failed in this run"))
            failed.add(n)
            continue
        if not has_snapshot(scenario.setup):
            skipped.append((n, f"no snapshot `{scenario.setup}`; run {dep} first"))
            failed.add(n)
            continue
        print(f"\n[{n}] {scenario.title} …", flush=True)
        result = await run_scenario(scenario, out / n, args)
        results.append(result)
        print(f"[{n}] {'PASS' if result['passed'] else 'FAIL'} {result['counts']}", flush=True)
        if result["passed"]:
            snapshot("save", n)
        else:
            failed.add(n)
        if not args.no_findings:
            add_finding(scenario, result, run_id)
        write_report(out / "report.md", results, skipped, commit, run_id)
    write_report(out / "report.md", results, skipped, commit, run_id)
    print(f"\nReport: {out / 'report.md'}")


if __name__ == "__main__":
    asyncio.run(main())
