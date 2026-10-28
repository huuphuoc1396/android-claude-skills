---
name: run-scenarios
description: Run the skill scenarios in scenarios/ end to end with no one at the keyboard — a tester session plays the user from each scenario file, a judge session ticks the checklist — and report what passed and failed. Use when asked to run, rerun, or test scenarios, or to check a skill after changing it.
---

# Run scenarios

[`scenarios/run.sh`](../../../scenarios/run.sh) does the work: for each scenario it restores the snapshot
the Setup names, runs the skill in a Claude Code session in `sample-app/`, has a tester session follow the
scenario (setup steps, prompts, answers to every question), has a judge session mark each `- [ ]` item, and
saves the scenario's snapshot when it passes. Everything happens in a sandbox clone,
`scenarios/.runs/sandbox`, synced from this working tree (uncommitted skill edits included), so its
commits, branches, and `snap/*` tags never reach this repository.

## 1. Pick the scenarios

From the request:
- "all" or nothing specific → `all`.
- Numbers → those numbers (`03 10 04`).
- A skill that changed → `skill:<name>`: its scenarios plus every scenario that builds on their output.

Show the plan with `scenarios/run.sh <selection> --dry-run`. A scenario whose snapshot doesn't exist in the
sandbox and whose source scenario isn't in the plan is skipped; say so, and offer to add the missing ones.

Before starting, tell the user in one short message:
- The order, and that a full run takes hours (forge scenarios build and test the app) and many tokens.
- Scenario 18 reads the real TMDB Figma file and needs `FIGMA_TOKEN` in the environment; without it the
  tester skips it. `--no-figma` hides the token from every session.
- The subject uses Claude Code's default model unless they name one (`--model`); tester and judge use
  `sonnet` (`--tester-model`, `--judge-model`).

Wait for a go-ahead, since the run is long and costly. Skip the wait when the request already says to run.

## 2. Run

Run `scenarios/run.sh <selection> [options]` in the background and wait for it to finish; don't poll.
Each scenario prints a `PASS` / `FAIL` line as it ends.

## 3. Report

Read `scenarios/.runs/<run-id>/report.md` (the last line of the output names it). Then tell the user:
- A table: scenario, PASS / FAIL / SKIPPED, counts.
- For each failure: the failed checks with the judge's evidence. Open `<NN>/judge.json` and `<NN>/log.md`
  to tell a skill bug from a scenario or runner problem (a tester that misread the script, a check the
  judge couldn't see). Name which one it is.
- `unclear` checks, briefly: they need a human look or a better check in the scenario.
- That the runner appended a dated line to each scenario's **Findings** (uncommitted), and the usage rows
  in `usage.csv` (`usage.py show`).

Propose fixes to skills or scenarios, but don't edit them unless asked. Don't commit.

## Notes

- Rerunning one scenario reuses the sandbox snapshots from earlier runs. After a skill change, run with
  `skill:<name>` so the later snapshots are refreshed too.
- `--no-findings` leaves the scenario files untouched.
- Sessions in the run can't push, use the web, or write outside the sandbox and the temp directory; the
  runner denies those calls and logs them.
