# Title Discovery — Design

Status: Confirmed
Requirement: [task.md](task.md)
Fidelity: mockup
Source: https://www.figma.com/design/G4YiZkobsY5gJP94z8pFlh/TMDB--Community- (page `0:1`; frames Onboard `1:2`, Home `29:2`, Show `47:2`)
Captured: 2026-10-28

Fidelity is `mockup`: final Portuguese copy, colors, and icons, but sample content (posters, placeholder
cast names) and no loading, empty, error, offline, or search designs. Anything marked _proposed_ below was
added from task.md and has no design yet.

## Screens
| Screen | File | States |
|---|---|---|
| Onboard | [onboard](screens/onboard.md) | Default |
| Home | [home](screens/home.md) | Content; Loading, Empty, Error, Cached notice _(proposed)_ |
| Search results | [search-results](screens/search-results.md) _(proposed, no design)_ | Loading, Results, No results, Offline, Error |
| Show (Title details) | [show](screens/show.md) | Content, Favorited _(proposed)_; Loading, Error, Cached notice, No trailer _(proposed)_ |

## Navigation
| From | Trigger | To | Back | Source |
|---|---|---|---|---|
| onboard | tap "Acessar" | home | removes onboard (back from home exits the app) | prototype: dissolve, 300 ms, ease in-out |
| home | tap a featured poster | show (that title) | to home | prototype: slide in from the right, 300 ms, ease out |
| home | tap a "Mais populares" poster | show (that title) | to home | proposed (confirmed 2026-10-28) |
| home | submit a query in the search field | search-results | to home, query kept | proposed |
| search-results | tap a result | show (that title) | to search-results | proposed |
| show | tap "Voltar" or system back | previous screen | — | prototype: back |
| show | tap a recommendation | show (that title) | to the previous show | proposed |
| show | tap "Assistir trailer" | YouTube app or browser (external) | returns to show | proposed |
| home | tap profile avatar | none (AC-12) | — | proposed |

Entry points: app launcher. First launch opens onboard; every later launch opens home (AC-1).

The featured carousel's prototype link sits on the whole carousel frame; each poster opens its own title.

## Visual tokens
The Figma file defines no color, text, or effect styles and the project theme has no matching tokens, so
these are the raw values used in the frames. Font is Inter throughout.

| Name used in screen files | Value |
|---|---|
| bg gradient | linear, #303243 at 0% → #15151D at 65% (Home, Show background) |
| accent purple gradient | linear, #8000FF → #540BA1 (buttons) |
| header purple gradient | linear, #8000FF → transparent (Home header, 522 pt tall) |
| accent pink | #FF1F8A (selected tab, rating arc) |
| search fill | #36076B |
| surface | #303243 (chips, rating track, favorite button) |
| surface deep | #15161D (rating inner disc) |
| text primary | #FFFFFF |
| text secondary | #CCCCCC (synopsis) |
| text tertiary | #BBBBBB (date, runtime) |

## Assets
| Name | Type | Source | Status | Used in | Notes |
|---|---|---|---|---|---|
| img_onboarding_bg | png | Figma 29:46 → assets/img_onboarding_bg.png | needs-export | onboard | exported file is a Dune still, reference only; designer supplies licensed artwork |
| img_logo_tmdb | vector | Figma 34:11 → assets/img_logo_tmdb.svg | needs-export | onboard | exported file is a custom "THE MOVIE DB" wordmark, reference only; use TMDB's official logo per their attribution rules |
| ic_search | vector | Figma 29:16 → assets/ic_search.svg | provided | home | Iconly Light Outline "Search", white |
| ic_profile | vector | — | needs-export | home | generic avatar for the profile entry point; the design shows a person photo |
| ic_back | vector | Figma 47:115 → assets/ic_back.svg | provided | show | left arrow, white |
| ic_favorite | vector | Figma 47:103 → assets/ic_favorite.svg | provided | show | heart outline, white |
| ic_favorite_filled | vector | — | needs-export | show | filled heart for the Favorited state _(proposed)_ |
| ic_clock | vector | Figma 47:98 → assets/ic_clock.svg | provided | show | runtime, #BBBBBB |
| ic_play | vector | Figma 47:112 → assets/ic_play.svg | provided | show | play in circle, white |

Status: `exists` (in the project) · `reuse` (icon library, name only) · `provided` (file in assets/) ·
`needs-export` (no file yet). Names follow Android prefixes: `ic_`, `img_`, `bg_`, `anim_`.

Posters, backdrops, and cast photos come from TMDB at runtime and are not assets.

## Cross-check
- ⚠ Home shows a 5th tab "Documentários" that task.md does not include → dropped; the design keeps 4 tabs.
- ⚠ "Mais populares" posters have no prototype link, but AC-5 needs them to open details → they open show.
- ⚠ Recommendations on show have no prototype link (AC-5) → they open show for that title _(proposed)_.
- ⚠ No search results design (AC-6) → [search-results](screens/search-results.md) _(proposed)_.
- ⚠ No loading, empty, error, offline, or cached-notice designs (task.md UI states, EC-1 to EC-4, EC-6) →
  proposed per screen; designer to confirm.
- ⚠ No Favorited state (AC-8) → filled heart in accent pink _(proposed)_; designer to confirm.
- ⚠ Profile avatar shows a real person's photo, but profile is out of scope (AC-12) → generic avatar icon
  `ic_profile`, no action.
- ⚠ The Onboarding artwork and wordmark can't ship as exported (licensing, TMDB attribution) → both
  `needs-export` from the designer.
- ⚠ The design accent uses both purple and pink; task.md says "purple accents" → keep both, as designed.
- ⚠ The "Mais populares" sample under the Filmes tab shows TV series → sample content only; lists follow AC-2/AC-3.
- ⚠ Cast names are a repeated placeholder ("Sandra Bullock"); genre chip "Thriler" is sample content →
  both come from TMDB.
- ⚠ Only portrait 375 × 812 frames exist; EC-7 covers rotation → landscape layout is an open question.
- ⚠ No trailer (AC-9): the "Assistir trailer" button is hidden, so nothing needs a design.

## Open questions
- [ ] Designer: confirm or redesign the proposed states (loading, empty, error, offline, cached notice,
  Favorited) and the search results screen.
- [ ] Designer: licensed onboarding artwork and the official TMDB logo; where else TMDB attribution appears.
- [ ] Designer: generic profile avatar icon.
- [ ] Product: review the proposed English copy in each screen file.
- [ ] Designer: landscape layout, or keep portrait layouts stretched to width.
- [ ] Product: are cast members tappable? (task.md open question; assumed no)

## Change log
- 2026-10-28: Initial version.
