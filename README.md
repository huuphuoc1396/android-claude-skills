# Android Claude Skills

[Claude Code](https://claude.com/claude-code) skills for taking a feature in an Android project from idea to implemented code.

## Skills

| Skill | What it does |
| --- | --- |
| [scout](skills/scout/SKILL.md) | Explores an Android codebase and generates (or updates) a concise `CLAUDE.md`. |
| [rulebook](skills/rulebook/SKILL.md) | Generates path-scoped rule files in `.claude/rules/`, one per architectural layer. |
| [spark](skills/spark/SKILL.md) | Clarifies a feature idea through rounds of questions, then writes `docs/features/<feature>/task.md`. |
| [slice](skills/slice/SKILL.md) | Optional: splits a large `task.md` into ordered, reviewable vertical-slice subtasks. |
| [blueprint](skills/blueprint/SKILL.md) | Writes an implementation plan of ordered, buildable steps mapped to acceptance criteria. |
| [forge](skills/forge/SKILL.md) | Executes an approved plan: production code and unit tests, compiling after each step. |
| [foundry](skills/foundry/SKILL.md) | Runs spark → [slice] → blueprint → forge end to end, in **Auto** mode (follows the agent's recommendations, asks only when needed) or **Interactive** mode (asks at every stage). |

## Workflow

```
scout → rulebook                       (once per repo)
spark → [slice] → blueprint → forge    (per feature)
foundry                                (the same, in one go)
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
