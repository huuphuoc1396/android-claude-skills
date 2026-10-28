# 16 - scout and rulebook: refresh existing files

Setup: `restore 02`. Then break the outputs by hand:
- In `CLAUDE.md`: rename `MainDispatcherRule` to `TestDispatcherRule` (wrong), add a Modules row for a
  `feature:profile` module that doesn't exist (stale), and delete the line naming `TMDB_API_KEY` (missing).
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
      catches all three changes.
- [ ] Keeps the parts that were right; the rest of the file is not reworded for no reason.
- [ ] Shows the full proposed file and writes only after the yes.

## Rulebook

Prompt, in a new session:

```
/rulebook refresh the rules
```

Approve the proposal.

- [ ] The proposal table marks each existing rule keep / update / merge / delete; only the database rule
      is update.
- [ ] Flags `core/**` with `git ls-files ':(glob)core/**'`: it matches modules other than `core:database`.
- [ ] Flags the missing reference path and replaces it with a file that exists.
- [ ] Writes only after approval and reports which files were updated.

## Findings

-
