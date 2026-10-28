# 03 - spark: bookmarks

Setup: output of 01.

Prompt:

```
/spark Users want to bookmark articles from the news list and see them on a separate Bookmarks
screen. Bookmarks must survive app restarts.
```

Answer at least one question with a non-recommended option, to check it is respected.

## Expect

- [ ] Asks in rounds of 2–4 multiple-choice questions, recommended option first.
- [ ] Covers goal, scope, behavior, edge cases (offline, article no longer in the feed, empty bookmarks),
      constraints.
- [ ] Posts a summary and waits for confirmation before writing.
- [ ] Writes `docs/features/<slug>/task.md` with numbered `AC-n` (Given/When/Then) and `EC-n`.
- [ ] No module, class, or library names in task.md.
- [ ] Runs no Gradle commands.
- [ ] Suggests `slice` as the next step (two screens + persistence) and doesn't run it.

## Findings

-
