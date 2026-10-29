# Android Claude Skills

[Claude Code](https://claude.com/claude-code) and [GitHub Copilot](https://docs.github.com/copilot/concepts/agents/about-agent-skills) skills for taking a feature in an Android project from idea to implemented code.

## Skills

| Skill | What it does |
| --- | --- |
| [scout](skills/scout/SKILL.md) | Explores an Android codebase and generates (or updates) a concise `CLAUDE.md`. |
| [rulebook](skills/rulebook/SKILL.md) | Generates path-scoped rule files in `.claude/rules/`, one per architectural layer. |
| [spark](skills/spark/SKILL.md) | Clarifies a feature idea through rounds of questions, then writes `docs/features/<feature>/task.md`. |
| [mold](skills/mold/SKILL.md) | Optional: reads the design once (Figma, screenshots, draw.io) and writes `design.md` plus one file per screen: layout, states, copy, navigation, assets. |
| [slice](skills/slice/SKILL.md) | Optional: splits a large `task.md` into ordered, reviewable vertical-slice subtasks. |
| [blueprint](skills/blueprint/SKILL.md) | Writes an implementation plan of ordered, buildable steps mapped to acceptance criteria. |
| [forge](skills/forge/SKILL.md) | Executes an approved plan: production code and unit tests, compiling after each step. |
| [foundry](skills/foundry/SKILL.md) | Runs spark → [mold] → [slice] → blueprint → forge end to end, in **Auto** mode (follows the agent's recommendations, asks only when needed) or **Interactive** mode (asks at every stage). |

## Workflow

```
scout → rulebook                                (once per repo)
spark → [mold] → [slice] → blueprint → forge    (per feature)
foundry                                         (the same, in one go)
```

When the requirement or design changes, run `spark` or `mold` again: they update their file and list
the subtasks and plans affected, which `slice` and `blueprint` then update without touching finished
work. `forge` resumes from the first unticked step, and `foundry` runs this whole update chain when it is
called again with changed input.

## Repository layout

```
skills/        # the skills; the only place to edit them
sample-app/    # small multi-module Android app for testing the skills
  .claude/skills -> ../../skills   (symlink, so the app always sees the current skills)
  .github/skills -> ../../skills   (same, for GitHub Copilot)
scenarios/     # step-by-step checks for each skill, run against sample-app
```

## Testing the skills

Open Claude Code in `sample-app/` and follow [scenarios/README.md](scenarios/README.md). The sample app is
a movie and TV browser built on [TMDB](https://www.themoviedb.org/) (Compose, Hilt, Room, Retrofit,
convention plugins in `build-logic/`) with four features, `feature:home`, `feature:details`,
`feature:search`, and `feature:onboarding`, plus core modules and unit tests.

Put a TMDB API key in the gitignored `sample-app/local.properties`. Without it the app still builds, but
every TMDB request fails with 401:

```properties
TMDB_API_KEY=<your key>
```

```bash
cd sample-app && ./gradlew assembleDebug testDebugUnitTest
```

## Install

Copy the skills into your user or project skills directory:

```bash
git clone https://github.com/huuphuoc1396/android-claude-skills.git
cp -r android-claude-skills/skills/* ~/.claude/skills/        # all projects
# or
cp -r android-claude-skills/skills/* <your-project>/.claude/skills/   # one project
```

Then invoke a skill in Claude Code, e.g. `/spark`.

## Use with GitHub Copilot

The skills are standard [Agent Skills](https://agentskills.io) (`SKILL.md` with `name` and `description`
frontmatter), which Copilot loads from `.github/skills/` in agent mode (VS Code, Copilot CLI, and the cloud
agent). No conversion is needed:

```bash
cp -r android-claude-skills/skills/* <your-project>/.github/skills/   # one project
cp -r android-claude-skills/skills/* ~/.copilot/skills/               # all projects
```

Then invoke one in Copilot chat, e.g. `/spark`, or just describe the task and Copilot picks the skill.

Things to know:

- Already have `.github/copilot-instructions.md` and `.github/instructions/<layer>.instructions.md`? Skip
  `scout` and `rulebook`. `spark`, `mold`, `slice`, `blueprint`, `forge`, and `foundry` use those files
  (rules matched by `applyTo`) wherever they would read `CLAUDE.md` and `.claude/rules/`. `scout` and
  `rulebook` themselves still write the Claude format.
- Where a skill says "subagent", Copilot uses its own sub-agent or runs the work inline; the result is the same.
- The scenario runner in `scenarios/` drives Claude Code only; the Copilot setup is not covered by it.
