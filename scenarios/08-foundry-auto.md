# 08 - foundry, Auto mode

Setup: output of 01 and 02, no `docs/`.

Prompt:

```
/foundry Auto mode, commit after each subtask. Users want to bookmark articles from the news list and
see them on a separate Bookmarks screen. Bookmarks must survive app restarts.
```

## Expect

- [ ] Doesn't ask the mode or commit questions (already answered in the prompt).
- [ ] Does not ask routine questions; records them as `(auto)` decisions in task.md and the plans.
- [ ] **Still stops to ask** about the database schema change (version bump + migration).
- [ ] Runs slice, then blueprint → forge per subtask, without waiting between subtasks.
- [ ] One commit per subtask, in the commit style from `CLAUDE.md`.
- [ ] Final report has an "Auto decisions to review" list with links.

## Findings

-
