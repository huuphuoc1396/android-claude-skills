# 04 - slice: My List

Setup: output of 03.

Prompt:

```
/slice docs/features/<slug>/task.md
```

When asked about something not needed for a first release, choose to take it out of the feature.

## Expect

- [ ] Shows the outline table in plain chat and stops — no question dialog in that turn.
- [ ] Subtask 01 is a vertical happy-path slice (e.g. open My List from the avatar + list favorites
      newest first), not "all data layer".
- [ ] Every AC/EC from task.md appears in the outline; split ones are listed below the table.
- [ ] Lists candidate merges with sizes.
- [ ] After "ok", asks the grouping question (multi-select) before writing files.
- [ ] Taking an AC out of the feature stops and points to `spark` instead of leaving it out of the
      subtasks.
- [ ] Files `subtasks/NN-<slug>.md` with `Status: Todo`; task.md gets a `## Subtasks` section.

## Findings

-
