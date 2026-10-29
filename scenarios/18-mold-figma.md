# 18 - mold: capture a real Figma design

Recaptures `title-discovery` from the Figma file it was designed in, and compares the result with the
design the baseline already holds. Needs `FIGMA_TOKEN` with access to the file (or a Figma MCP); without
it, skip the scenario.

The file, [TMDB (Community)](https://www.figma.com/design/G4YiZkobsY5gJP94z8pFlh/TMDB--Community-?node-id=0-1),
has a Cover page and an App Design page (`0:1`) with three frames: Onboard `1:2`, Home `29:2`, Show `47:2`
(Title details). Its prototype links: Onboard → Home (dissolve, 300 ms), the Home featured carousel → Show
(slide in, 300 ms), Show back arrow and "Voltar" → back. It defines no color or text styles.

Setup: `restore 02`. Then remove the baseline design from the feature (the runner already keeps a copy
of the baseline under `scenarios/.runs/reference-18`; don't create it):

```bash
rm -rf sample-app/docs/features/title-discovery/{design.md,screens,assets}
```

Prompt:

```
/mold title-discovery https://www.figma.com/design/G4YiZkobsY5gJP94z8pFlh/TMDB--Community-?node-id=0-1&p=f
```

Answer the cross-check questions when the subject asks them: drop the "Documentários" tab (task.md keeps
four); "Mais populares" posters and Show's recommendations open Show for that title; Search results stays
a proposed screen with no design. Don't volunteer these answers if a question isn't asked. Approve
design.md.

## Expect

- [ ] Says which access it uses (MCP or REST API) and never prints the token.
- [ ] Lists the frames with one structure call first; detail and screenshot calls go to the three screen
      frames one at a time, never to the whole page or file. With the REST API, raw responses are saved
      outside the repo and filtered with `jq` or a script before reading.
- [ ] Screens table: Onboard, Home, Show, plus Search results marked proposed with no design (in any
      row order). Show is tied to task.md's Title details.
- [ ] One file per designed screen, `screens/onboard.md`, `screens/home.md`, `screens/show.md`, each with
      `Key: Figma node` `1:2` / `29:2` / `47:2`, and screenshots `screens/onboard.png`, `home.png`,
      `show.png`.
- [ ] `Fidelity: mockup`.
- [ ] Navigation has the three prototype rows above with `Source: prototype` and their transitions; the
      rows task.md needs but the design lacks (Mais populares, recommendations, search) are `proposed`.
- [ ] Copy is verbatim, accents included: `"O que você quer assistir hoje?"`,
      `"Tudo sobre filmes, séries, animes e muito mais."`, `"Acessar"`, `"Assistir trailer"`,
      `"Elenco principal"`, `"Recomendações"`, `"Voltar"`.
- [ ] Visual tokens say the file has no styles and use the raw values from the frames (for example the
      purple `#8000FF` → `#540BA1` button gradient); no invented style names.
- [ ] Assets: the heart icons (`ic_favorite`, `ic_favorite_filled`) are `exists` now that the app has
      them, not `provided` or `needs-export`; the rest carry their Figma node ids.
- [ ] Cross-check flags the extra "Documentários" tab, "Mais populares" posters and recommendations with
      no prototype link, and Search results with no frame.
- [ ] Apart from the asset statuses, Screens, keys, and Navigation match the reference in
      `scenarios/.runs/reference-18/sample-app/docs/features/title-discovery/design.md`.
- [ ] No module, class, or Composable names; nothing written under `src/` or `res/`; runs no Gradle
      commands.

## Update with no change

Then, in a new session:

```
/mold title-discovery update https://www.figma.com/design/G4YiZkobsY5gJP94z8pFlh/TMDB--Community-?node-id=0-1&p=f
```

- [ ] Re-fetches only the structure (frame ids, names, sizes, text), not details or screenshots.
- [ ] Reports that nothing changed, and leaves design.md, the screen files, `Status`, and the Change log
      as they are.

## Findings

- 2026-10-28 (runner 20261028-172950): FAIL; fail: Lists frames with one structure call first; detail/screenshot calls go to the th …; fail: Screens table: Onboard, Home, Show, plus Search results marked proposed with no  …; fail: One file per designed screen with Key: Figma node 1:2/29:2/47:2, and screenshots …; fail: Visual tokens say the file has no styles, use raw values (#8000FF → #540BA1), no …; fail: Assets: heart icons are `exists` (ic_favorite, ic_favorite_filled in feature:det …; unclear: Apart from asset statuses, Screens, keys, and Navigation match the reference des …; fail: No module/class/Composable names; nothing under src/ or res/; no Gradle commands …; fail: Update: re-fetches only the structure (frame ids, names, sizes, text), not detai …
- 2026-10-29 (runner 20261029-102655): PASS; unclear: Apart from the asset statuses, Screens, keys, and Navigation match the reference
