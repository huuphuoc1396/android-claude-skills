# 05 - blueprint: My List subtask 01

Setup: `restore 04` (includes the rules from 02 and design.md from 10).

Prompt:

```
/blueprint docs/features/<slug>/subtasks/01-<slug>.md
```

If asked about a schema change: add a column for the time a title was favorited, with an auto-migration;
existing rows get 0 so they sort last. Approve the plan.

## Expect

- [ ] Reads `CLAUDE.md` and the matching `.claude/rules/` files.
- [ ] Schema: subtask 01 (profile entry with a count) needs none, so the plan says "no schema change" with
      that reason and leaves the database version alone. The favorited-at column (version 3 → 4,
      auto-migration, a default for existing rows) belongs to a later subtask and must not be planned here.
- [ ] Reuse table names real components that this subtask uses: `SampleTopAppBar`,
      `TestFavoritesRepository`, `MainDispatcherRule`. `SamplePoster` and `SampleEmptyState` are named
      only if a step uses them.
- [ ] Steps compile one by one; each has a Compile line with real Gradle tasks.
- [ ] A step that changes `FavoritesRepository` (a shared module's public API) also compiles the modules
      that depend on `core:data`, at least `core:testing` and `feature:details`.
- [ ] With design.md: Design mapping maps each Copy line to a string key in `values/` and `values-pt/`,
      and each Variant to how it is handled.
- [ ] Traceability table covers every AC/EC of the subtask.
- [ ] On approval: plan `Status: Approved`, subtask `Status: Planned`.

## Findings

- 2026-10-28 (runner 20261028-202331): FAIL; fail: Asks about the schema change (favorited-at column, DB 3→4, auto-migration, defau …; fail: Reuse table names SamplePoster, SampleEmptyState, SampleTopAppBar, TestFavorites …
- 2026-10-28 (runner 20261028-203038): PASS
