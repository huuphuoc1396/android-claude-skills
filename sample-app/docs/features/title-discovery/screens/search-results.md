# Search results

Key: search-results (proposed, no Figma frame)
Screenshots: none
Covers: AC-5, AC-6, EC-3

No design exists. This is a minimal proposal built from parts designed on home; replace it when the
designer delivers a frame.

## Layout
```
┌─────────────────────────┐
│ ←  ┌──────────────── 🔍┐│  back + search field (query kept)
│    └───────────────────┘│
│ ┌───┐ ┌───┐             │  poster grid, 2 columns,
│ │   │ │   │             │  scrolls vertically
│ └───┘ └───┘             │
│ ┌───┐ ┌───┐             │
└─────────────────────────┘
```

## States
| State | What is shown | Actions |
|---|---|---|
| Loading | Search field; poster placeholders | Edit query, back |
| Results | Poster grid of matches from all categories | Tap a poster → show, edit query, back |
| No results | Message | Edit query, back |
| Offline | Offline message with retry | Retry, back |
| Error | Error message with retry | Retry, back |

## Components
- Search field, Poster: as on home.
- Back: `ic_back` as on show.
- Background: bg gradient.

## Copy
| Key | pt-BR _(proposed)_ | en _(proposed)_ |
|---|---|---|
| No results | "Nenhum resultado para \"<query>\"" | "No results for \"<query>\"" |
| Offline | "Você está offline" | "You're offline" |
| Error | "Não foi possível carregar" | "Couldn't load" |
| Retry | "Tentar novamente" | "Try again" |

## Assets
- ic_back, ic_search

## Notes
- Search runs when the user submits the query (keyboard search action) _(proposed)_.
