# Show

Key: Figma node `47:2`
Screenshots: [default](show.png)
Covers: AC-5, AC-7, AC-8, AC-9, AC-10, AC-11, AC-13, EC-1, EC-2, EC-4, EC-5, EC-8, EC-9, EC-10

The design names this frame "Show"; task.md calls it Title details.

## Layout
```
┌─────────────────────────┐
│ ← Voltar           (♡)  │  over the backdrop
│                         │
│     backdrop artwork    │  493 pt, fades into the background
│                         │
│ (45%) Imperdoável (2021)│  rating ring + title
│       10/12/2021 (BR) • ⏱ 1h 53m
│ ─────────────────────── │  divider
│ Após cumprir pena por … │  synopsis
│ ┌─────────────────────┐ │
│ │ ▶ Assistir trailer  │ │  primary button
│ └─────────────────────┘ │
│ Elenco principal        │
│ (◯) (◯) (◯) (◯) (◯ …    │  cast row, scrolls horizontally
│ Categoria(s)            │
│ [Drama] [Thriler]       │  genre chips
│ Recomendações           │
│ ┌───┐ ┌───┐ ┌──         │  poster row, scrolls horizontally
└─────────────────────────┘
```
Frame is 375 × 1267: the screen scrolls vertically. 30 pt side margins, 315 pt content width. The title
block starts about 376 pt from the top, overlapping the bottom of the backdrop. Spacing: divider 20 pt
below the title block, synopsis 21 pt below, button 30 pt below, then 46, 23, and 41 pt before the cast,
genres, and recommendations sections.

## States
| State | What is shown | Actions |
|---|---|---|
| Content | Everything above | Back, favorite, trailer, tap recommendation → show |
| Favorited _(proposed)_ | Favorite button shows `ic_favorite_filled` in accent pink | Tap to unfavorite |
| No trailer _(proposed)_ | "Assistir trailer" button is hidden; synopsis is followed directly by "Elenco principal" | — |
| Missing fields _(proposed)_ | Missing rating, runtime, synopsis, cast, genres, or recommendations: that element or section is hidden; missing backdrop or poster: surface-color placeholder | — |
| Loading _(proposed)_ | Back and favorite over a surface-color backdrop placeholder; loading indicator below | Back |
| Error _(proposed)_ | Back over a placeholder; error message with "Tentar novamente" | Retry, back |
| Cached notice _(proposed)_ | Content plus the notice as on home, under the divider | Same as Content |
| Trailer can't open _(proposed)_ | Snackbar | Dismiss |

## Components
- Backdrop: image 375 × 493 at the top; over it a gradient from #21222E (opaque at 70% height) to
  transparent (at 81%), blending the lower edge into the background.
- Back: `ic_back` 24 pt + "Voltar" Inter 700 16 white, 6 pt gap, at 18 pt from the left and 36 pt from the top.
- Favorite button: 32 pt circle, surface color, `ic_favorite` 18 pt white centered; top right, 30 pt from
  the right edge, 32 pt from the top.
- Rating ring: 57 pt; surface-color track, accent pink arc showing the score (TMDB score × 10, as a
  percentage); 45 pt surface deep inner disc; label "45%" Inter 700 15 white, centered.
- Title: "<name> (<year>)", Inter 700 22, white, 16 pt right of the rating ring, wraps.
- Meta line: "<release date> (<country>)" • `ic_clock` 19 pt "<runtime>", Inter 400 16, text tertiary;
  4 pt dot separator. Date is dd/MM/yyyy in pt and MM/dd/yyyy in en; runtime as "1h 53m".
- Divider: 1 pt line, 311 pt wide.
- Synopsis: Inter 400 14, text secondary, line height 17.5, full text (no truncation in the design).
- Primary button: as on onboard, 44 pt tall, with `ic_play` 20 pt left of the label, 10 pt gap.
- Section heading: as on home.
- Cast item: 60 pt circle photo, name below (7 pt gap) Inter 700 12 white, centered, max 2 lines, 75 pt
  wide; items 79 pt apart. Not tappable.
- Genre chip: height 31, corner radius 34, surface color, horizontal padding 17, label Inter 500 15 white;
  14 pt apart; wraps to new lines.
- Recommendations: Poster row as on home.

## Copy
| Key | pt-BR | en _(proposed)_ |
|---|---|---|
| Back | "Voltar" | "Back" |
| Trailer | "Assistir trailer" | "Watch trailer" |
| Cast heading | "Elenco principal" | "Main cast" |
| Genres heading | "Categoria(s)" | "Genres" |
| Recommendations heading | "Recomendações" | "Recommendations" |
| Trailer can't open _(proposed)_ | "Não foi possível abrir o trailer" | "Couldn't open the trailer" |
| Favorite (screen reader) _(proposed)_ | "Adicionar aos favoritos" / "Remover dos favoritos" | "Add to favorites" / "Remove from favorites" |

Sample content in the frame (title, date, runtime, synopsis, "Sandra Bullock" × 7 _placeholder_,
"Drama", "Thriler") comes from TMDB.

## Assets
- ic_back, ic_favorite, ic_favorite_filled, ic_clock, ic_play

## Notes
- No designer notes in the frame.
