# 15 - which skill a plain request loads

Checks the `description` lines: each request, typed without a slash command, loads the right skill or none.
Run after changing any skill's `description`.

Setup: `restore 02`.

Start a new session for each prompt and stop it (Esc) as soon as you see which skill it loads, or that it
answers without one. Nothing needs to be written.

| Prompt | Expected skill |
| --- | --- |
| `Users want to share a title with a friend from Title details.` | spark |
| `Refresh CLAUDE.md, the module list may be out of date.` | scout |
| `Add a rule file for the network layer.` | rulebook |
| `Break docs/features/title-discovery/task.md into smaller pieces.` | slice |
| `Write an implementation plan for showing the runtime on Title details.` | blueprint |
| `Implement docs/features/title-discovery/implement-plans/01-home-browse.md.` | forge |
| `Here is the wireframe for My List: ../scenarios/fixtures/my-list.drawio` | mold |
| `Take "users can rate a title" from idea to working code in one go.` | foundry |
| `What does HomeViewModel do?` | none |
| `Run the unit tests for feature:search.` | none |
| `Why does the build need TMDB_API_KEY?` | none |

Without a session per prompt, the same check runs headless; the loaded skill shows as a `Skill` tool call:

```bash
claude -p "<prompt>" --output-format stream-json --verbose --max-turns 2 | grep -o '"skill":"[^"]*"'
```

## Expect

- [ ] Every row loads its expected skill, and the last three load none.
- [ ] No row loads a second skill on top of the expected one.

## Findings

- 2026-10-28 (runner 20261028-172950): PASS
- 2026-10-29 (runner 20261029-080318): PASS
