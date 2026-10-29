# 03 - spark: My List

Setup: `restore 02`.

Prompt:

```
/spark Users want to see the titles they favorited on a separate My List screen, opened from the
profile avatar on Home, newest first. The list must work offline.
```

When asked whether this updates `title-discovery`, answer that it is a new feature. On the next question
after that, pick the second option and note the question in Findings, to check a non-recommended answer
is respected. Confirm the summary.

## Expect

- [ ] Lists `docs/features/` and asks whether this is an update to `title-discovery` (where favorites
      were added) or a new feature.
- [ ] Notices the existing favorite heart on Title details and builds on it instead of inventing a
      second way to save titles.
- [ ] Asks in rounds of 2–4 multiple-choice questions, recommended option first.
- [ ] Covers goal, scope, behavior, edge cases (offline, empty list, a title unfavorited on Title details
      while My List is open, process death), constraints (Portuguese and English copy).
- [ ] Posts a summary and waits for confirmation before writing.
- [ ] Writes `docs/features/<slug>/task.md` with numbered `AC-n` (Given/When/Then) and `EC-n`.
- [ ] No module, class, or library names in task.md.
- [ ] `Status: Confirmed` only if no open question is needed before the next step.
- [ ] Runs no Gradle commands.
- [ ] Suggests `slice` as the next step (new screen, Home entry point, persistence) and doesn't run it.

## Findings

- 2026-10-28 (runner 20261028-172950): PASS
- 2026-10-28 (runner 20261028-191825): PASS
