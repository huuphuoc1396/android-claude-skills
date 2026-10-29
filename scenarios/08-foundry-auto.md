# 08 - foundry, Auto mode

Setup: `restore 02` (no `docs/features/<slug>/` for My List yet). Start on `main`, to check that
foundry doesn't commit there. Delete the branch it creates afterwards.

Prompt:

```
/foundry Auto mode, commit after each subtask. Users want to see the titles they favorited on a
separate My List screen, opened from the profile avatar on Home, newest first. The list must work
offline.
```

When it stops on the schema change, answer as in 05. Approve the change to `FavoritesRepository`.

## Expect

- [ ] Doesn't ask the mode or commit questions (already answered in the prompt).
- [ ] Creates a feature branch (CLAUDE.md's convention or `feature/<slug>`) before the first commit and
      says so; nothing is committed to `main`.
- [ ] Does not ask routine questions; records them as `(auto)` decisions in task.md and the plans.
- [ ] **Still stops to ask** about the database schema change (version bump + migration) and the change
      to `FavoritesRepository`'s public API.
- [ ] Runs slice, then blueprint → forge per subtask, each subtask in its own subagent, one at a time,
      without waiting between subtasks.
- [ ] A subtask whose plan forge leaves `In progress` because a check failed or an AC/EC has a gap stops
      the run with forge's report: no commit, no next subtask.
- [ ] A subtask left `In progress` only with `Manual verification: pending` doesn't stop the run: it is
      committed (when the user chose commits) and the next subtask starts, without asking.
- [ ] Each subtask gets its own commit; the subjects are imperative sentences without a prefix such as
      `feat:`, as in `CLAUDE.md`. One extra commit for the docs written by spark and slice is fine, and a
      `Co-Authored-By` trailer is not graded.
- [ ] Final report has an "Auto decisions to review" list with links, and one section gathering every plan
      with `Manual verification: pending` and its steps (a heading of equivalent meaning is fine).

## Findings

- 2026-10-28 (runner 20261028-172950): FAIL; fail: Still stops to ask about the schema change (version bump + migration) and the Fa …; fail: Final report has an "Auto decisions to review" list with links
- 2026-10-29 (runner 20261029-050658): FAIL; fail: Still stops to ask about the schema change (version bump + migration) and the Fa …; unclear: A plan left In progress because a check failed or an AC/EC has a gap stops the r …; fail: One commit per subtask, in the commit style from CLAUDE.md
- 2026-10-29 (runner 20261029-070844): PASS; unclear: A subtask left In progress because of a failed check or AC/EC gap stops the run
