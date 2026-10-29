# 16 - scout and rulebook: refresh existing files

Setup: `restore 02`. Then break the outputs by hand:
- In `CLAUDE.md`: rename `MainDispatcherRule` to `TestDispatcherRule` (wrong), add a Modules row for a
  `feature:profile` module that doesn't exist (stale), and delete the Setup line that names
  `TMDB_API_KEY` (missing).
- In the database rule under `.claude/rules/`: change its path glob to `core/**`, and one reference path to
  `core/database/src/main/kotlin/Missing.kt`.

## Scout

Prompt:

```
/scout refresh CLAUDE.md
```

Approve the proposed file.

- [ ] Reads the existing `CLAUDE.md` and verifies it instead of rewriting from scratch.
- [ ] Summary grouped as **Wrong**, **Missing**, **Stale**, each with a file path as evidence, and it
      catches all three changes: the renamed rule (wrong), the `feature:profile` row for a module that
      doesn't exist (stale), and `TMDB_API_KEY` absent from Setup (missing).
- [ ] Keeps the parts that were right; the rest of the file is not reworded for no reason.
- [ ] Shows the full proposed file and writes only after the yes.

## Rulebook

Prompt, in a new session:

```
/rulebook refresh the rules
```

Approve the proposal.

- [ ] The proposal table has an Action column with values from keep / update / merge / delete for each
      existing rule; the database rule is update. Other rules may be update too when the subject gives a
      reason. Not every value has to appear.
- [ ] Flags that `core/**` matches modules other than `core:database`, checked against the files in git
      (any command that lists them), and proposes a narrower glob.
- [ ] Flags the missing reference path and replaces it with a file that exists.
- [ ] Writes only after approval and reports which files were updated.

## Findings

- 2026-10-28 (runner 20261028-172950): FAIL; fail: Rulebook proposal table marks each rule keep/update/merge/delete; only the datab …; fail: Flags core/** with git ls-files ':(glob)core/**': it matches modules other than  …
- 2026-10-29 (runner 20261029-072313): FAIL; fail: Summary grouped Wrong/Missing/Stale with file path evidence, catches all three c …; fail: Shows full proposed file and writes only after the yes
- 2026-10-29 (runner 20261029-073229): FAIL; fail: Summary grouped Wrong/Missing/Stale with file path evidence, catches all three c …; fail: Rulebook proposal table marks keep/update/merge/delete; database rule is update
- 2026-10-29 (runner 20261029-073957): PASS
