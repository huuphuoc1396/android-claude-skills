# 12 - slice: update subtasks after a requirement change

Setup: `restore 11` (task.md approved again, `Status: Confirmed`). Plan subtask 02 with
`/blueprint docs/features/<slug>/subtasks/02-<slug>.md`, approve it, and `save 12-setup`, so one
subtask is `Planned`. Rerun from `restore 12-setup`.

Prompt:

```
/slice docs/features/<slug>/task.md
```

Put the long-press removal criteria where slice recommends. Approve the changes.

## Expect

- [ ] Goes to the Updating flow instead of slicing from scratch.
- [ ] Subtask 01 (`Done`) is not rewritten; a change to its scope becomes a new subtask.
- [ ] The new AC/EC lands in a `Todo` subtask or in a new subtask with the next free `NN`; nothing is
      renumbered.
- [ ] If subtask 02 changes, it goes back to `Todo` and its plan is listed for `blueprint` Updating.
- [ ] Every AC/EC in task.md is covered by exactly one subtask (or split, stated).
- [ ] Shows the changes in chat and waits for approval; no file is deleted without it.
- [ ] Updates task.md's `## Subtasks` links; doesn't edit any plan.

## Findings

- 2026-10-28 (runner 20261028-214156): PASS
