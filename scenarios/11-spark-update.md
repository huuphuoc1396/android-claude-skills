# 11 - spark: change a requirement

Setup: `restore 06` (subtask 01 of My List `Done`, the other subtasks `Todo`).

Prompt:

```
/spark <slug>: users can remove a title from My List with a long-press, with Undo in a snackbar.
```

Answer that removing from My List also clears the heart on Title details. Accept the defaults the
subject records as `(assumed)`. Approve the change.

## Expect

- [ ] Goes to the Updating flow; doesn't ask whether this is a new feature.
- [ ] Asks 2–4 questions about what the change leaves unclear, including whether removing also clears the
      heart on Title details; doesn't ask what the request already states.
- [ ] Shows the change as added / changed / removed with IDs, and waits for approval.
- [ ] New criteria take the next free `AC-n` / `EC-n`; existing IDs are not renumbered.
- [ ] Adds a Change log line and sets `Status: Draft` until re-approved.
- [ ] Lists the affected subtasks and plans, pointing to `slice` for subtasks and `blueprint` for plans,
      and edits neither.

## Findings

- 2026-10-28 (runner 20261028-212037): FAIL; fail: Goes to the Updating flow; doesn't ask whether this is a new feature.; fail: Asks only about what the change leaves unclear (e.g. does removing also clear th …
- 2026-10-28 (runner 20261028-213143): FAIL; fail: Asks only about what the change leaves unclear
- 2026-10-28 (runner 20261028-213334): FAIL; fail: Asks only about what the change leaves unclear (e.g. does removing also clear th …
- 2026-10-28 (runner 20261028-213755): FAIL; fail: Asks 2–4 questions about what the change leaves unclear, including whether remov …
- 2026-10-28 (runner 20261028-213955): PASS
