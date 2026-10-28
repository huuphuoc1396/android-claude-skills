# 07 - small change skips slice

Setup: output of 01.

Prompt:

```
/spark Show how many titles the search found above the Search results grid, like "24 results".
```

When asked whether this updates `title-discovery`, answer that it is a new feature.

## Expect

- [ ] Asks whether this is an update to `title-discovery` (which has Search) or a new feature.
- [ ] Few questions (plural forms, Portuguese copy, what shows with no results or while loading).
- [ ] Suggests `blueprint` directly, not `slice`.
- [ ] `/blueprint` on the task produces `implement-plan.md` (not `implement-plans/`) with one or two steps,
      reusing the titles already in `SearchResults.Found` and adding plurals in `values/` and `values-pt/`.

## Findings

-
