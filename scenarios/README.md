# Scenarios

Repeatable checks for the skills, run against [`sample-app/`](../sample-app). Each file says what to set
up, what to type, and what must be true afterwards.

## Running a scenario

1. Reset `sample-app/` to the baseline (see below), unless the scenario says to keep earlier output.
2. Open Claude Code in `sample-app/` — the skills load through the `.claude/skills` symlink, so edits in
   [`skills/`](../skills) apply on the next session with no copying.
3. Type the scenario's prompt and answer questions as the scenario says.
4. Tick the checklist. Note anything that failed in the scenario's **Findings** section with the date.

## Baseline

The `sample-baseline` git tag marks `sample-app/` with no skill output (no `CLAUDE.md`, `.claude/rules/`,
or `docs/`). Reset to it with:

```bash
git restore --source=sample-baseline -- sample-app && git clean -fd -- sample-app
```

Create or move the tag after any intentional change to the sample app:

```bash
git tag -f sample-baseline
```

## Order

Scenarios build on each other's output: run them in number order, or reset and replay the earlier ones.

| # | Skill | Needs |
| --- | --- | --- |
| [01](01-scout.md) | scout | baseline |
| [02](02-rulebook.md) | rulebook | 01 |
| [03](03-spark-bookmarks.md) | spark | 01 |
| [04](04-slice-bookmarks.md) | slice | 03 |
| [05](05-blueprint-bookmarks.md) | blueprint | 02, 04 |
| [06](06-forge-bookmarks.md) | forge | 05 |
| [07](07-spark-small-change.md) | spark → blueprint (no slice) | 01 |
| [08](08-foundry-auto.md) | foundry, Auto mode | 01, 02 |
| [09](09-foundry-interactive.md) | foundry, Interactive mode | 01, 02 |
