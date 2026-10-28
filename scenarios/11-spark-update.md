# 11 - spark: change a requirement

Setup: `restore 06` (subtask 01 of My List `Done`, the other subtasks `Todo`).

Prompt:

```
/spark <slug>: users can remove a title from My List with a long-press, with Undo in a snackbar.
```

Answer that removing from My List also clears the heart on Title details. Approve the change.

## Expect

- [ ] Goes to the Updating flow; doesn't ask whether this is a new feature.
- [ ] Asks only about what the change leaves unclear (e.g. does removing also clear the heart on Title
      details).
- [ ] Shows the change as added / changed / removed with IDs, and waits for approval.
- [ ] New criteria take the next free `AC-n` / `EC-n`; existing IDs are not renumbered.
- [ ] Adds a Change log line and sets `Status: Draft` until re-approved.
- [ ] Lists the affected subtasks and plans, pointing to `slice` for subtasks and `blueprint` for plans,
      and edits neither.

## Findings

-
