# Scenarios

Repeatable checks for the skills, run against [`sample-app/`](../sample-app). Each file says what to set
up, what to type, and what must be true afterwards.

## Running a scenario

1. Restore the state the scenario's **Setup** names, e.g. `restore 02` means
   `scenarios/snapshot.sh restore 02` (see [Snapshots](#snapshots)).
2. Open Claude Code in `sample-app/` — the skills load through the `.claude/skills` symlink, so edits in
   [`skills/`](../skills) apply on the next session with no copying.
3. Type the scenario's prompt and answer as the scenario says. Any question it doesn't answer: pick the
   recommended option and note the question in **Findings**, so the next run can answer it the same way.
4. Tick the checklist. Note anything that failed in **Findings** with the date.
5. If it passed and later scenarios restore it, save it: `scenarios/snapshot.sh save <number>`.
6. Record what the run used: `scenarios/usage.py record <number>` reads the newest `sample-app/` session
   (subagents included), prices it at API rates, and appends it to `scenarios/usage.csv`.
   `scenarios/usage.py show [<number>]` lists every run and the average per scenario.

## Baseline

The `sample-baseline` git tag marks `sample-app/` with no `CLAUDE.md` or `.claude/rules/`. Its `docs/`
holds one finished feature, `title-discovery` (Home, Title details, Onboarding, Search, favorites), so
skills meet an existing feature as in a real project. Restore it with `scenarios/snapshot.sh restore
baseline`.

Create or move the tag after any intentional change to the sample app:

```bash
git tag -f sample-baseline
```

## Snapshots

[`snapshot.sh`](snapshot.sh) saves `sample-app/` as a local tag `snap/<name>` pointing at a commit on no
branch, so a scenario can start from the output of the one before it without replaying the whole chain.
`restore` removes everything in `sample-app/` that the snapshot doesn't have, except gitignored files
(`local.properties`, `build/`).

```bash
scenarios/snapshot.sh save 03
scenarios/snapshot.sh restore 03
scenarios/snapshot.sh list
```

The tags stay local; don't push them (`git push --tags`). After a skill change, re-save the snapshots of
that skill's scenarios and of every scenario after it in the chain, since their output may differ.

## Order

Run the chain below once to create the snapshots, in this order (10 runs before 04 so slice and blueprint
see the design):

```
baseline → 01 → 02 → 03 → 10 → 04 → 05 → 06 → 11 → 12 → 13
```

After that, each scenario only needs the snapshot its Setup names. To check a changed skill, run its
scenarios and the ones after them in the chain.

| # | Skill | Setup |
| --- | --- | --- |
| [01](01-scout.md) | scout | baseline |
| [02](02-rulebook.md) | rulebook | 01 |
| [03](03-spark-my-list.md) | spark | 02 |
| [04](04-slice-my-list.md) | slice | 10 |
| [05](05-blueprint-my-list.md) | blueprint | 04 |
| [06](06-forge-my-list.md) | forge | 05 |
| [07](07-spark-small-change.md) | spark → blueprint (no slice) | 02 |
| [08](08-foundry-auto.md) | foundry, Auto mode | 02 |
| [09](09-foundry-interactive.md) | foundry, Interactive mode | 02 |
| [10](10-mold-my-list.md) | mold, from a draw.io wireframe | 03 |
| [11](11-spark-update.md) | spark, changing a requirement | 06 |
| [12](12-slice-update.md) | slice, updating subtasks | 11, then 12-setup |
| [13](13-blueprint-update-forge-resume.md) | blueprint update, forge resume | 12, then 13-setup |
| [14](14-foundry-changed-input.md) | foundry, resume with changed input | 06 |
| [15](15-triggers.md) | which skill a plain request loads | 02 |
| [16](16-scout-rulebook-refresh.md) | scout and rulebook, refreshing their files | 02 |
| [17](17-mold-figma-access.md) | mold, Figma access and token rules | 03 |
