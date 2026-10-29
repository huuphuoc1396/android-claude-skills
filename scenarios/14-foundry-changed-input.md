# 14 - foundry: resume with a changed requirement

Setup: `restore 06` (subtask 01 of My List `In progress` with `Manual verification: pending`, the others
`Todo`; foundry resumes a feature whatever started it).

Prompt, in a new session:

```
/foundry <slug> Auto mode, don't commit: also show the genre under each poster on My List.
```

## Expect

- [ ] Treats the input as a change request, not as a resume only.
- [ ] Runs spark's Updating flow, then slice's, then blueprint's for each affected plan, before any new
      code.
- [ ] Subtask 01 and its ticked steps stay as they are, are not redone, and 01 is listed in a section of
      the final report for plans awaiting manual verification, with its "How to test" steps.
- [ ] Then continues with the first subtask by number that is neither `Done` nor awaiting manual
      verification (02).
- [ ] In Auto mode, the update decisions appear as `(auto)` in the final report.

## Findings

- 2026-10-28 (runner 20261028-215943): FAIL; fail: Treats the input as a change request, not as a resume only.; fail: Runs spark's Updating flow, then slice's, then blueprint's for each affected pla …; fail: `Done` subtasks and ticked steps stay as they are.; fail: In Auto mode, the update decisions appear as `(auto)` in the final report.
- 2026-10-28 (runner 20261028-221822): FAIL; fail: Subtask 01 and its ticked steps stay as they are, are not redone, and 01 is list …
- 2026-10-29 (runner 20261029-002600): FAIL; fail: Subtask 01 and its ticked steps stay as they are, are not redone, and 01 is list …; fail: In Auto mode, the update decisions appear as `(auto)` in the final report.
- 2026-10-29 (runner 20261029-012149): PASS
