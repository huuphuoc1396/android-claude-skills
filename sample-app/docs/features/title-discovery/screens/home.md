# Home

Key: Figma node `29:2`
Screenshots: [default](home.png)
Covers: AC-2, AC-3, AC-4, AC-5, AC-6, AC-11, AC-12, EC-1, EC-4, EC-6, EC-7

## Layout
```
┌─────────────────────────┐
│ O que você quer     (◯) │  heading + profile avatar
│ assistir hoje?          │
│ ┌─────────────────── 🔍┐│  search field
│ └──────────────────────┘│
│ (Filmes) Séries Animes N│  category tabs, scroll horizontally
│ ┌───┐ ┌───┐ ┌──         │  featured carousel, scrolls horizontally
│ │   │ │   │ │           │
│ └───┘ └───┘ └──         │
│ Mais populares          │  section heading
│ ┌───┐ ┌───┐ ┌──         │  popular row, scrolls horizontally
│ └───┘ └───┘ └──         │
└─────────────────────────┘
```
30 pt side margins; rows bleed off the right edge. Vertical spacing: heading top at 64 pt, search 17 pt
below the heading, tabs 31 pt below search, featured carousel 37 pt below tabs, "Mais populares" 23 pt
below the carousel, popular row 16 pt below its heading. The screen scrolls vertically when content
exceeds the height.

## States
| State | What is shown | Actions |
|---|---|---|
| Content | All sections for the selected tab | Switch tab, search, tap a poster → show |
| Loading _(proposed)_ | Heading, search, tabs; poster placeholders (surface color, 124 × 188, radius 8) in both rows | Switch tab, search |
| Empty _(proposed)_ | Heading, search, tabs; message in place of both rows | Switch tab, search |
| Error _(proposed)_ | Heading, search, tabs; error message with a "Tentar novamente" button (primary button) | Retry, switch tab, search |
| Cached notice _(proposed)_ | Content plus a one-line notice under the tabs, text tertiary color | Same as Content |
| Refresh failed _(proposed)_ | Content stays; a snackbar with the error | Dismiss |

## Components
- Background: bg gradient; header purple gradient across the top 522 pt behind the heading, search, and tabs.
- Heading: Inter 700 26, white, line height 31.5, max 2 lines, 288 pt wide.
- Profile avatar: 40 pt circle, white stroke, drop shadow; shows `ic_profile`, no action.
- Search field: 315 × 46, corner radius 32, search fill; placeholder Inter 500 15, white at reduced
  opacity (estimated from screenshot); `ic_search` 20 pt at the right, 17 pt from the edge.
- Category tabs: text tabs Inter 400 16, white at reduced opacity (estimated from screenshot), about 29 pt
  apart. Selected tab: pill, accent pink, corner radius 70, padding 14 × 6, label Inter 600 16 white.
- Poster: 124 × 188, corner radius 8, image only (no title text), 12–14 pt apart. Used in the featured
  carousel, "Mais populares", search-results, and show recommendations.
- Section heading: Inter 700 18, white. Used again on show.

## Copy
| Key | pt-BR | en _(proposed)_ |
|---|---|---|
| Heading | "O que você quer assistir hoje?" | "What do you want to watch today?" |
| Search placeholder | "Buscar" | "Search" |
| Tabs | "Filmes", "Séries", "Animes", "Novelas" | "Movies", "Series", "Anime", "Telenovelas" |
| Section | "Mais populares" | "Most popular" |
| Empty _(proposed)_ | "Nada por aqui ainda" | "Nothing here yet" |
| Error _(proposed)_ | "Não foi possível carregar" | "Couldn't load" |
| Retry _(proposed)_ | "Tentar novamente" | "Try again" |
| Cached notice _(proposed)_ | "Você está offline. O conteúdo pode estar desatualizado." | "You're offline. Content may be out of date." |

## Assets
- ic_search, ic_profile

## Notes
- No designer notes in the frame.
- The design has a 5th tab "Documentários" (off-screen); it is not built (see design.md cross-check).
