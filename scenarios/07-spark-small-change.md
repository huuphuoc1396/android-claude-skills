# 07 - small change skips slice

Setup: `restore 02`.

Prompt:

```
/spark Show how many titles the search found above the Search results grid, like "24 results".
```

When asked whether this updates `title-discovery`, answer that it is a new feature.
Confirm the summary, then run `/blueprint docs/features/<slug>/task.md` and approve the plan.

## Expect

- [ ] Asks whether this is an update to `title-discovery` (which has Search) or a new feature.
- [ ] Few questions: at most about 5 content questions in one or two rounds, not counting the update-or-new
      question, the slug, and the confirmations. They cover plural forms and what shows with no results or
      while loading; Portuguese copy may be settled with an `(assumed)` default instead of a question.
- [ ] Suggests `blueprint` directly, not `slice`.
- [ ] `/blueprint` on the task produces `implement-plan.md` (not `implement-plans/`) with a few steps (at
      most three; a separate tests step is fine), reusing the titles already in `SearchResults.Found` and adding plurals in `values/` and `values-pt/`.

## Findings

- 2026-10-28 (runner 20261028-172950): PASS
- 2026-10-29 (runner 20261029-080318): FAIL; fail: /blueprint produces implement-plan.md (not implement-plans/) with one or two ste …
- 2026-10-29 (runner 20261029-083100): FAIL; fail: Few questions (plural forms, Portuguese copy, what shows with no results or whil …
- 2026-10-29 (runner 20261029-094107): PASS
