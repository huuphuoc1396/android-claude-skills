# 13 - blueprint update and forge resume

Setup: `restore 12`. In a session, run
`/forge docs/features/<slug>/implement-plans/02-<slug>.md Do only Step 1 in this session, then stop.`
Check that the plan is `In progress` with Step 1 ticked (`✅`) and Step 2 not; otherwise redo from
`restore 12`. Then `save 13-setup`, and rerun from `restore 13-setup`.

Prompt, in a new session:

```
/blueprint docs/features/<slug>/implement-plans/02-<slug>.md update: show the title name under each
poster in one line instead of two.
```

## Expect

- [ ] Goes to the Updating flow; reads which steps are ticked.
- [ ] Explores and asks only about the affected part.
- [ ] Ticked steps are not rewritten; the change becomes a new step at the end or a suffixed step
      (`Step 3b`); nothing is renumbered.
- [ ] Updates Traceability, adds a Change log line, and sets `Status: Draft`.
- [ ] Says forge should resume from the first unticked step.

Approve the plan, then:

```
/forge docs/features/<slug>/implement-plans/02-<slug>.md
```

- [ ] Doesn't ask to confirm the plan again (it is `Approved`), and doesn't start from Step 1.
- [ ] Compiles the modules of the ticked steps first, then starts at the first unticked step.
- [ ] `Status: Done` only after the tests pass and Traceability is covered; otherwise stays `In progress`.

## Findings

- 2026-10-28 (runner 20261028-215211): PASS
