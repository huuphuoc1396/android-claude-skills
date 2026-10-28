# 12 - slice: update subtasks after a requirement change

Setup: output of 11, with task.md approved again (`Status: Confirmed`). Before running, plan subtask 02
with `/blueprint` and approve it, so one subtask is `Planned`.

Prompt:

```
/slice docs/features/<slug>/task.md
```

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

-
