# 14 - foundry: resume with a changed requirement

Setup: output of 08 or 09 stopped partway, with at least one My List subtask `Done` and one not.

Prompt, in a new session:

```
/foundry <slug>: also show the release year under each poster on My List.
```

## Expect

- [ ] Treats the input as a change request, not as a resume only.
- [ ] Runs spark's Updating flow, then slice's, then blueprint's for each affected plan, before any new
      code.
- [ ] `Done` subtasks and ticked steps stay as they are.
- [ ] Then continues with the first subtask by number that is not `Done`.
- [ ] In Auto mode, the update decisions appear as `(auto)` in the final report.

## Findings

-
