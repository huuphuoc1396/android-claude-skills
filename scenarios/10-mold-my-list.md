# 10 - mold: My List

Setup: `restore 03` (Confirmed task.md). The design is [`fixtures/my-list.drawio`](fixtures/my-list.drawio),
a wireframe with deliberate gaps:
- `Sort list` has no flow leading to it.
- `tap poster → Title details` points at no screen in this design (Title details already exists in the app).
- A note asks for long-press to remove with Undo, which task.md from 03 likely doesn't have.
- The empty-state illustration exists nowhere in the project.
- `My List / Landscape` is a variant of My List, not a screen or a state.

Prompt:

```
/mold <slug> ../scenarios/fixtures/my-list.drawio
```

Answer the cross-check questions: Sort is out of scope; a poster opens the existing Title details.
Approve design.md. Don't add long-press removal to task.md here: scenario 11 adds it through `spark`.
Then `save 10`, before the Update part.

## Expect

- [ ] Reads task.md first; does not ask again what task.md answers.
- [ ] Asks in rounds of 2–4 multiple-choice questions, recommended option first.
- [ ] Cross-check flags the orphan `Sort list` (no flow leads to it), the `Title details` destination
      with no screen, and long-press removal as a mismatch with task.md (missing from it, or drawn
      although task.md rules it out of scope).
- [ ] Writes `design.md` with `Fidelity: wireframe`, a Screens table, a Navigation table
      (From / Trigger / To / Back / Source) with rows marked `drawn ⚠`, an Assets table, and Cross-check.
- [ ] One file per screen: `screens/my-list.md` (Empty as a state of My List, not its own file), plus
      `screens/profile.md` marked proposed when task.md requires a Profile entry the design doesn't draw
      (a `home.md` for the Home excerpt is optional); none for Sort once ruled out of scope.
- [ ] `screens/my-list.md` has a Variants table: Landscape `yes` (5 columns); Dark and Large font `no`.
- [ ] Copy is verbatim, diacritics included (`"Minha lista"`, `"Sua lista está vazia"`,
      `"Removido da Minha lista"`, `"Desfazer"`).
- [ ] Assets: the heart icons are `exists` (`ic_favorite`, `ic_favorite_filled`); the empty illustration
      is `needs-export`.
- [ ] No module, class, or Composable names; nothing written under `src/` or `res/`.
- [ ] Runs no Gradle commands.
- [ ] Suggests updating task.md through `spark` for long-press removal, and doesn't edit task.md itself.
- [ ] Suggests the next step without running it: `slice` for a large task, or `blueprint` with a reason
      for a small one (My List is small, so `blueprint` is fine). Scenario 04 runs `slice` itself.

## Update

Then change the fixture (rename the `My List` screen to `Favorites`, in the frame titles and the top bar)
and run:

```
/mold <slug> update ../scenarios/fixtures/my-list.drawio
```

- [ ] Shows the change as a short added / changed / removed list and waits for approval.
- [ ] Asks whether to rename `screens/my-list.md` instead of creating a second file.
- [ ] Adds a Change log line and sets `Status: Draft`.
- [ ] If subtasks or plans exist, lists them and points to `slice` and `blueprint`.

Before restoring, copy the `Status:` line and the Change log lines of `design.md` into your notes, so the
judge can check them.

Afterwards, put both back so later scenarios use the My List design:
`git restore scenarios/fixtures/my-list.drawio` and `restore 10`.

## Findings

- 2026-10-28 (runner 20261028-172950): FAIL; fail: One file per screen: home.md (or reasoned name) and my-list.md; none for Sort; fail: my-list.md Variants: Landscape yes (5 columns); Dark and Large font no; fail: Assets: heart icons exist; empty illustration needs-export; fail: Suggests updating task.md through spark for long-press removal, and doesn't edit …; fail: Suggests slice as next step and doesn't run it; unclear: Update: shows change as short added/changed/removed list and waits for approval; unclear: Update: adds Change log line and sets Status: Draft
- 2026-10-28 (runner 20261028-192532): FAIL; fail: One file per screen: home.md and my-list.md (Empty as a state); none for Sort; fail: my-list.md Variants: Landscape yes (5 columns); Dark and Large font no; fail: Assets: heart icons exist (ic_favorite, ic_favorite_filled); empty illustration  …; fail: Suggests updating task.md through spark for long-press removal, and doesn't edit …; fail: Suggests slice as the next step and doesn't run it; unclear: Update: adds a Change log line and sets Status: Draft
- 2026-10-28 (runner 20261028-193508): FAIL; fail: Update: shows change as short added/changed/removed list and waits for approval
- 2026-10-28 (runner 20261028-193930): FAIL; fail: Cross-check flags the orphan Sort list, Title details destination with no screen …
- 2026-10-28 (runner 20261028-195155): FAIL; fail: Cross-check flags orphan Sort list, Title details destination with no screen, lo …; fail: Assets: heart icons exist (ic_favorite, ic_favorite_filled); empty illustration  …; fail: No module, class, or Composable names; nothing under src/ or res/
- 2026-10-28 (runner 20261028-200505): PASS; unclear: Update: shows change as short added/changed/removed list and waits for approval
