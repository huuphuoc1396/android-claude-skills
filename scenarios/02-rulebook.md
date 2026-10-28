# 02 - rulebook

Setup: output of 01 (`CLAUDE.md` exists).

Prompt:

```
/rulebook
```

## Expect

- [ ] Proposes a layer table first (file, layer, globs, modules, reference files) and waits.
- [ ] Layers roughly: designsystem, feature, data, network, database, datastore, testing, build-logic.
- [ ] Globs checked with `git ls-files ':(glob)<glob>'`; `core/database/**` doesn't also match feature code.
- [ ] Each rule 30–80 lines; every reference path exists.
- [ ] database rule's "Adding a new entity" covers entity → DAO → database version + auto-migration →
      schema export.
- [ ] No content duplicated from `CLAUDE.md`.
- [ ] Offers to add a `.claude/rules/` pointer to `CLAUDE.md` if missing.

## Findings

-
