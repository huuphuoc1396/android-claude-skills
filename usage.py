#!/usr/bin/env python3
"""Measure the tokens and API-equivalent cost of each session at the repository root, and keep every run in
scenarios/usage.csv.

Reads a session's Claude Code transcript (plus its subagent transcripts), sums usage per model, prices it,
and writes one row per model to scenarios/usage.csv. Rows are keyed by session, so recording a session
again replaces its rows. The scenario number is found by matching the session's first prompt against the
`Prompt:` block of each scenario file; a session that matches none is recorded with scenario `-`.

  usage.py hook
      # Stop hook in .claude/settings.json: reads the session from stdin after every turn
  usage.py record [<scenario>] [--session <id|path>] [--note "..."]
      # by hand; default session: the newest one at the repository root
  usage.py show [<scenario>]
      # every recorded run, then the average per scenario

Prices are USD per million tokens (Anthropic API rates). On a Pro/Max subscription nothing is billed per
token, so read the cost as "what this run would cost on the API".
"""
import argparse
import csv
import datetime
import difflib
import json
import pathlib
import re
import subprocess
import sys
from collections import defaultdict

ROOT = pathlib.Path(__file__).resolve().parent
SCENARIOS = ROOT / "scenarios"
CSV = SCENARIOS / "usage.csv"
PROJECTS = pathlib.Path.home() / ".claude" / "projects"
SESSIONS_DIR = PROJECTS / ("-" + str(ROOT).strip("/").replace("/", "-").replace(".", "-"))

# input, output, cache read. Cache writes are 1.25x input (5 min TTL) or 2x input (1 hour TTL).
PRICES = {
    "claude-fable-5-1": (10.00, 50.00, 0.25),
    "claude-fable-5": (10.00, 50.00, 1.00),
    "claude-opus-5-5": (4.00, 20.00, 0.20),
    "claude-opus-5": (5.00, 25.00, 0.50),
    "claude-opus-4-8": (5.00, 25.00, 0.50),
    "claude-sonnet-5-5": (2.00, 10.00, 0.20),
    "claude-sonnet-5": (2.00, 10.00, 0.20),
    "claude-sonnet-4-6": (3.00, 15.00, 0.30),
    "claude-haiku-5-5": (0.10, 0.50, 0.01),
    "claude-haiku-4-5": (1.00, 5.00, 0.10),
}
FAST_MULTIPLIER = 2.0
MATCH_THRESHOLD = 0.8

FIELDS = ["date", "scenario", "prompt", "session", "commit", "model", "requests", "input", "output",
          "cache_write_5m", "cache_write_1h", "cache_read", "total_tokens", "cost_usd", "note"]
COUNTS = ["requests", "input", "output", "cache_write_5m", "cache_write_1h", "cache_read"]


def find_session(arg):
    if arg:
        path = pathlib.Path(arg)
        if path.suffix == ".jsonl" and path.exists():
            return path
        path = SESSIONS_DIR / f"{arg}.jsonl"
        if path.exists():
            return path
        sys.exit(f"No transcript for session {arg}")
    sessions = sorted(SESSIONS_DIR.glob("*.jsonl"), key=lambda p: p.stat().st_mtime)
    if not sessions:
        sys.exit(f"No transcripts in {SESSIONS_DIR}")
    return sessions[-1]


def first_prompt(transcript):
    for line in transcript.open():
        try:
            entry = json.loads(line)
        except json.JSONDecodeError:
            continue
        if entry.get("type") != "user" or entry.get("isMeta"):
            continue
        content = entry["message"]["content"]
        if not isinstance(content, str):
            content = " ".join(b.get("text", "") for b in content if b.get("type") == "text")
        # A slash command is stored as tags; turn it back into what was typed.
        command = re.search(r"<command-name>(.*?)</command-name>", content)
        if command:
            args = re.search(r"<command-args>(.*?)</command-args>", content, re.S)
            content = f"{command.group(1)} {args.group(1) if args else ''}"
        return normalize(content)
    return ""


def normalize(text):
    return " ".join(text.split())


def detect_scenario(prompt):
    best, best_ratio = "-", 0.0
    for path in sorted(SCENARIOS.glob("[0-9][0-9]-*.md")):
        block = re.search(r"^Prompt:\s*\n+```[^\n]*\n(.*?)\n```", path.read_text(), re.S | re.M)
        if not block:
            continue
        ratio = difflib.SequenceMatcher(None, prompt, normalize(block.group(1))).ratio()
        if ratio > best_ratio:
            best, best_ratio = path.name[:2], ratio
    return best if best_ratio >= MATCH_THRESHOLD else "-"


def collect(transcript):
    files = [transcript, *sorted(transcript.with_suffix("").glob("subagents/*.jsonl"))]
    # One API response is written once per content block, each line repeating the same usage.
    responses = {}
    for path in files:
        for line in path.open():
            try:
                entry = json.loads(line)
            except json.JSONDecodeError:
                continue
            message = entry.get("message") or {}
            usage = message.get("usage")
            if entry.get("type") != "assistant" or not usage or message.get("model") == "<synthetic>":
                continue
            key = (message.get("id"), entry.get("requestId"))
            responses[key] = (message["model"], usage)

    totals = defaultdict(lambda: defaultdict(float))
    for model, usage in responses.values():
        cache = usage.get("cache_creation") or {}
        write_1h = cache.get("ephemeral_1h_input_tokens", 0)
        write_5m = cache.get("ephemeral_5m_input_tokens", usage.get("cache_creation_input_tokens", 0) - write_1h)
        t = totals[model]
        t["requests"] += 1
        t["input"] += usage.get("input_tokens", 0)
        t["output"] += usage.get("output_tokens", 0)
        t["cache_write_5m"] += write_5m
        t["cache_write_1h"] += write_1h
        t["cache_read"] += usage.get("cache_read_input_tokens", 0)
        t["cost_usd"] += cost(model, usage.get("input_tokens", 0), usage.get("output_tokens", 0),
                              write_5m, write_1h, usage.get("cache_read_input_tokens", 0),
                              usage.get("speed") == "fast")
    return totals


def cost(model, inp, out, write_5m, write_1h, read, fast):
    price = PRICES.get(model)
    if price is None:
        print(f"warning: no price for {model}, counted as $0", file=sys.stderr)
        return 0.0
    p_in, p_out, p_read = price
    usd = (inp * p_in + out * p_out + write_5m * p_in * 1.25 + write_1h * p_in * 2 + read * p_read) / 1e6
    return usd * (FAST_MULTIPLIER if fast else 1)


def write_run(transcript, scenario=None, note=None):
    """Replace the session's rows in usage.csv.

    Returns (scenario, tokens, cost before, cost now) for the session, or None when it has no usage yet.
    """
    totals = collect(transcript)
    if not totals:
        return None
    prompt = first_prompt(transcript)
    rows = list(csv.DictReader(CSV.open())) if CSV.exists() else []
    previous = [r for r in rows if r["session"] == transcript.stem]
    rows = [r for r in rows if r["session"] != transcript.stem]
    # Keep what was set by hand on an earlier record of this session.
    scenario = scenario or (previous[0]["scenario"] if previous else None) or detect_scenario(prompt)
    note = note if note is not None else (previous[0]["note"] if previous else "")
    commit = subprocess.run(["git", "-C", str(ROOT), "rev-parse", "--short", "HEAD"],
                            capture_output=True, text=True).stdout.strip()
    date = datetime.datetime.fromtimestamp(transcript.stat().st_mtime).strftime("%Y-%m-%d %H:%M")
    for model, t in sorted(totals.items()):
        rows.append({
            "date": date, "scenario": scenario, "prompt": prompt[:80], "session": transcript.stem,
            "commit": commit, "model": model, **{k: int(t[k]) for k in COUNTS},
            "total_tokens": int(sum(t[k] for k in COUNTS[1:])), "cost_usd": f"{t['cost_usd']:.4f}", "note": note,
        })
    with CSV.open("w", newline="") as f:
        writer = csv.DictWriter(f, FIELDS)
        writer.writeheader()
        writer.writerows(rows)
    return (scenario,
            sum(int(sum(t[k] for k in COUNTS[1:])) for t in totals.values()),
            sum(float(r["cost_usd"]) for r in previous),
            sum(t["cost_usd"] for t in totals.values()))


def record(args):
    transcript = find_session(args.session)
    if write_run(transcript, args.scenario, args.note) is None:
        sys.exit(f"No usage in {transcript}")
    print(f"Recorded session {transcript.stem}")
    show(argparse.Namespace(scenario=None, session=transcript.stem))


def hook(_):
    # Never fail the turn: a broken transcript line or a locked CSV only costs one update.
    try:
        event = json.load(sys.stdin)
        run = write_run(pathlib.Path(event["transcript_path"]))
    except Exception as error:
        print(f"usage.py: {error}", file=sys.stderr)
        return
    if run:
        scenario, tokens, before, now = run
        label = f"scenario {scenario}" if scenario != "-" else "session"
        # systemMessage is shown to the user under the reply; it doesn't enter Claude's context.
        print(json.dumps({"systemMessage": f"Usage: this turn ${now - before:.2f} · "
                                           f"{label} {tokens / 1e6:.1f}M tokens, ${now:.2f}"}))


def show(args):
    if not CSV.exists():
        sys.exit("No runs recorded yet")
    rows = list(csv.DictReader(CSV.open()))
    if args.scenario:
        rows = [r for r in rows if r["scenario"] == args.scenario]
    if getattr(args, "session", None):
        rows = [r for r in rows if r["session"] == args.session]

    runs = defaultdict(lambda: {"tokens": 0, "output": 0, "cost": 0.0, "models": []})
    for r in rows:
        run = runs[(r["scenario"], r["session"], r["date"], r["commit"], r["prompt"], r["note"])]
        run["tokens"] += int(r["total_tokens"])
        run["output"] += int(r["output"])
        run["cost"] += float(r["cost_usd"])
        run["models"].append(r["model"].removeprefix("claude-"))

    print(f"\n{'scenario':<10}{'date':<18}{'commit':<9}{'tokens':>12}{'output':>10}{'cost $':>10}  models / prompt / note")
    for (scenario, _, date, commit, prompt, note), run in sorted(runs.items(), key=lambda kv: (kv[0][0], kv[0][2])):
        label = ", ".join(run["models"]) + (f"  {prompt[:40]}" if scenario == "-" else "") + (f"  — {note}" if note else "")
        print(f"{scenario:<10}{date:<18}{commit:<9}{run['tokens']:>12,}{run['output']:>10,}{run['cost']:>10.2f}  {label}")

    per_scenario = defaultdict(list)
    for (scenario, *_), run in runs.items():
        per_scenario[scenario].append(run["cost"])
    if len(runs) > 1:
        print(f"\n{'scenario':<10}{'runs':>6}{'avg $':>10}{'min $':>10}{'max $':>10}")
        for scenario, costs in sorted(per_scenario.items()):
            print(f"{scenario:<10}{len(costs):>6}{sum(costs) / len(costs):>10.2f}{min(costs):>10.2f}{max(costs):>10.2f}")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("hook").set_defaults(func=hook)
    rec = sub.add_parser("record")
    rec.add_argument("scenario", nargs="?")
    rec.add_argument("--session")
    rec.add_argument("--note")
    rec.set_defaults(func=record)
    sh = sub.add_parser("show")
    sh.add_argument("scenario", nargs="?")
    sh.set_defaults(func=show)
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
