# 05 - blueprint: My List subtask 01

Setup: `restore 04` (includes the rules from 02 and design.md from 10).

Prompt:

```
/blueprint docs/features/<slug>/subtasks/01-<slug>.md
```

On the schema change: add a column for the time a title was favorited, with an auto-migration; existing
rows get 0 so they sort last. Approve the plan.

## Expect

- [ ] Reads `CLAUDE.md` and the matching `.claude/rules/` files.
- [ ] Asks about the schema change: newest first needs the time a title was favorited, so `favorites`
      gets a column (database version 3 → 4, auto-migration, a default for existing rows) — or proposes
      a reasoned alternative.
- [ ] Reuse table names real components: `SamplePoster`, `SampleEmptyState`, `SampleTopAppBar`,
      `TestFavoritesRepository`, `MainDispatcherRule`.
- [ ] Steps compile one by one; each has a Compile line with real Gradle tasks.
- [ ] A step that changes `FavoritesRepository` (a shared module's public API) also compiles the modules
      that depend on `core:data`, at least `core:testing` and `feature:details`.
- [ ] With design.md: Design mapping maps each Copy line to a string key in `values/` and `values-pt/`,
      and each Variant to how it is handled.
- [ ] Traceability table covers every AC/EC of the subtask.
- [ ] On approval: plan `Status: Approved`, subtask `Status: Planned`.

## Findings

-
