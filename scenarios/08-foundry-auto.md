# 08 - foundry, Auto mode

Setup: output of 01 and 02, no `docs/features/<slug>/` for My List. Start on `main`, to check that
foundry doesn't commit there. Delete the branch it creates afterwards.

Prompt:

```
/foundry Auto mode, commit after each subtask. Users want to see the titles they favorited on a
separate My List screen, opened from the profile avatar on Home, newest first. The list must work
offline.
```

## Expect

- [ ] Doesn't ask the mode or commit questions (already answered in the prompt).
- [ ] Creates a feature branch (CLAUDE.md's convention or `feature/<slug>`) before the first commit and
      says so; nothing is committed to `main`.
- [ ] Does not ask routine questions; records them as `(auto)` decisions in task.md and the plans.
- [ ] **Still stops to ask** about the database schema change (version bump + migration) and the change
      to `FavoritesRepository`'s public API.
- [ ] Runs slice, then blueprint → forge per subtask, each subtask in its own subagent, one at a time,
      without waiting between subtasks.
- [ ] A subtask whose plan forge leaves `In progress` stops the run with forge's report: no commit, no
      next subtask.
- [ ] One commit per subtask, in the commit style from `CLAUDE.md`.
- [ ] Final report has an "Auto decisions to review" list with links.

## Findings

-
