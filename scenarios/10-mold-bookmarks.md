# 10 - mold: bookmarks

Setup: output of 03 (Confirmed task.md). The design is [`fixtures/bookmarks.drawio`](fixtures/bookmarks.drawio),
a wireframe with deliberate gaps:
- `Bookmark Settings` has no flow leading to it.
- `tap item → Article` points at no screen (the app opens articles in the browser).
- A note asks for swipe-to-remove with Undo, which task.md from 03 likely doesn't have.
- The empty-state illustration exists nowhere in the project.

Prompt:

```
/mold <slug> ../scenarios/fixtures/bookmarks.drawio
```

Answer the cross-check questions: Settings is out of scope; Article opens in the browser as today.

## Expect

- [ ] Reads task.md first; does not ask again what task.md answers.
- [ ] Asks in rounds of 2–4 multiple-choice questions, recommended option first.
- [ ] Cross-check flags the orphan `Bookmark Settings`, the `Article` destination with no screen, and
      swipe-to-remove as a requirement missing from task.md.
- [ ] Writes `design.md` with `Fidelity: wireframe`, a Screens table, a Navigation table
      (From / Trigger / To / Back / Source) with rows marked `drawn ⚠`, an Assets table, and Cross-check.
- [ ] One file per screen: `screens/spaceflight-news.md` and `screens/bookmarks.md` (Empty as a state of
      Bookmarks, not its own file); none for Settings once ruled out of scope.
- [ ] Copy is verbatim (`"No bookmarks yet"`, `"Bookmark removed"`, `"Undo"`).
- [ ] Assets: bookmark icons are `reuse` (icon library) or `exists`; the empty illustration is
      `needs-export`.
- [ ] No module, class, or Composable names; nothing written under `src/` or `res/`.
- [ ] Runs no Gradle commands.
- [ ] Suggests updating task.md through `spark` for swipe-to-remove, and doesn't edit task.md itself.
- [ ] Suggests `slice` as the next step and doesn't run it.

## Update

Then change the fixture (rename the `Bookmarks` screen to `Saved articles`) and run:

```
/mold <slug> update ../scenarios/fixtures/bookmarks.drawio
```

- [ ] Shows the change as a short added / changed / removed list and waits for approval.
- [ ] Asks whether to rename `screens/bookmarks.md` instead of creating a second file.
- [ ] Adds a Change log line and sets `Status: Draft`.

Restore the fixture afterwards: `git restore scenarios/fixtures/bookmarks.drawio`.

## Findings

-
