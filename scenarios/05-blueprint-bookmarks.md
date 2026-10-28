# 05 - blueprint: bookmarks subtask 01

Setup: output of 02 and 04.

Prompt:

```
/blueprint docs/features/<slug>/subtasks/01-<slug>.md
```

## Expect

- [ ] Reads `CLAUDE.md` and the matching `.claude/rules/` files.
- [ ] Asks about the schema change (new table vs column, database version 1 → 2, migration).
- [ ] Reuse table names real components: `SampleEmptyState`, `TestArticleRepository`, `MainDispatcherRule`.
- [ ] Steps compile one by one; each has a Compile line with real Gradle tasks.
- [ ] A step that changes `ArticleRepository` (a shared module's public API) also compiles `feature:news`
      and `core:testing`.
- [ ] Traceability table covers every AC/EC of the subtask.
- [ ] On approval: plan `Status: Approved`, subtask `Status: Planned`.

## Findings

-
