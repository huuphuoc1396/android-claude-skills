# Android Claude Skills

[Claude Code](https://claude.com/claude-code) skills for taking a feature in an Android project from idea to implemented code.

## Skills

| Skill | What it does |
| --- | --- |
| [onboard-codebase](skills/onboard-codebase/SKILL.md) | Explores an Android codebase and generates (or updates) a concise `CLAUDE.md`. |
| [generate-rules](skills/generate-rules/SKILL.md) | Generates path-scoped rule files in `.claude/rules/`, one per architectural layer. |
| [brainstorming](skills/brainstorming/SKILL.md) | Clarifies a feature idea through rounds of questions, then writes `docs/features/<feature>/task.md`. |
| [breakdown](skills/breakdown/SKILL.md) | Optional: splits a large `task.md` into ordered, reviewable vertical-slice subtasks. |
| [implement-plan](skills/implement-plan/SKILL.md) | Writes an implementation plan of ordered, buildable steps mapped to acceptance criteria. |
| [implement](skills/implement/SKILL.md) | Executes an approved plan: production code and unit tests, compiling after each step. |

## Workflow

```
onboard-codebase → generate-rules          (once per repo)
brainstorming → [breakdown] → implement-plan → implement   (per feature)
```

## Install

Copy the skills into your user or project skills directory:

```bash
git clone https://github.com/huuphuoc1396/android-claude-skills.git
cp -r android-claude-skills/skills/* ~/.claude/skills/        # all projects
# or
cp -r android-claude-skills/skills/* <your-project>/.claude/skills/   # one project
```

Then invoke a skill in Claude Code, e.g. `/brainstorming`.
