# 04 - slice: bookmarks

Setup: output of 03.

Prompt:

```
/slice docs/features/<slug>/task.md
```

## Expect

- [ ] Shows the outline table in plain chat and stops — no question dialog in that turn.
- [ ] Subtask 01 is a vertical happy-path slice (e.g. bookmark from the list + persisted), not "all data layer".
- [ ] Lists candidate merges with sizes.
- [ ] After "ok", asks the grouping question (multi-select) before writing files.
- [ ] Every AC/EC from task.md lands in exactly one subtask, or is marked partial.
- [ ] Files `subtasks/NN-<slug>.md` with `Status: Todo`; task.md gets a `## Subtasks` section.

## Findings

-
