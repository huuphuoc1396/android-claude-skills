# Title Discovery

Status: Confirmed
Source: chat (2026-10-28); Figma https://www.figma.com/design/G4YiZkobsY5gJP94z8pFlh/TMDB--Community- (Onboarding `1:2`, Home `29:2`, Title details `47:2`)

## Goal
Portuguese- and English-speaking users want a quick way to decide what to watch across films, series,
anime, and novelas. The app lets them browse and search titles from The Movie Database (TMDB), see the
details that matter for choosing, and keep browsing what they have already seen when offline. Success:
a user can go from launch to a title's details and trailer in a few taps, online or (for seen content)
offline.

## Scope
### In scope
- Onboarding screen, shown on first launch only.
- Home: search field, category tabs (Films, Series, Anime, Novelas), featured carousels, "Mais populares"
  section, profile entry point (visible only).
- Search results across all categories.
- Title details: artwork, name, release year, rating, runtime, synopsis, main cast, genres,
  recommendations, back navigation, favorite toggle, "Assistir trailer".
- Offline access to previously loaded content.
- Portuguese (Brazil) and English.
- Dark theme with purple accents.
- Removing the existing News feature; the app becomes single-purpose.

### Out of scope
- Profile screen; the profile icon has no action.
- Favorites list screen (favorites are only visible as the toggle on Title details).
- In-app trailer player.
- User accounts, sign-in, or syncing favorites across devices.
- In-app language switch.
- Pre-downloading details of titles the user has not opened.
- Searching while offline.

## User flow
1. On first launch the user sees Onboarding and taps "Acessar".
2. Home opens with the first category tab selected, showing featured carousels and "Mais populares".
3. The user switches category tabs, scrolls the lists, or submits a query in the search field to see search
   results.
4. The user taps a title (on Home, in search results, or in recommendations) and Title details opens.
5. On Title details the user can favorite or unfavorite the title, open the trailer, or open a
   recommended title.
6. Back returns to the previous screen.
7. On later launches the app opens directly on Home.

## UI states
| State | When | What the user sees / can do |
|---|---|---|
| Home – Loading | First load of a category with nothing cached | Loading indicator; can switch tabs or search |
| Home – Content | Lists available (from cache or network) | Carousels and "Mais populares"; can open titles, switch tabs, search |
| Home – Empty | A category returns no titles | Empty message for that tab; can switch tabs or search |
| Home – Error | Load failed and nothing cached | Error message with retry |
| Cached / offline notice | Content shown from cache while offline | Content plus a notice that it may be outdated |
| Search – Loading | Query submitted, waiting for results | Loading indicator; can edit the query |
| Search – Results | Matches found | List of titles; can open one |
| Search – No results | Query matches nothing | "No results" message; can edit the query |
| Search – Offline | No network | Offline message with retry |
| Details – Loading | Opening a title with nothing cached | Loading indicator; can go back |
| Details – Content | Details available (from cache or network) | All details; can favorite, open trailer (if any), open recommendations, go back |
| Details – Error | Load failed and nothing cached | Error message with retry; can go back |

## Acceptance criteria
- **AC-1** Onboarding on first launch only
  - Given the app is launched for the first time
  - When the user taps "Acessar"
  - Then Home opens, and on every later launch the app opens directly on Home.
- **AC-2** Category tabs
  - Given Home is shown
  - When the user selects Films, Series, Anime, or Novelas
  - Then the featured carousels and "Mais populares" show titles of that category only.
- **AC-3** Category rules
  - Given titles come from TMDB
  - Then Films are movies; Series are TV shows; Anime are titles in the Animation genre originating from
    Japan (movies and TV); Novelas are TV shows in the Soap genre originating from Brazil or Latin America.
- **AC-4** Home sections
  - Given a category is selected and titles are available
  - Then Home shows featured poster carousels and a "Mais populares" section for that category.
- **AC-5** Open details
  - Given a title is shown on Home, in search results, or in recommendations
  - When the user taps it
  - Then Title details for that title opens, and back returns to the previous screen.
- **AC-6** Search
  - Given the user is online
  - When the user submits a query in the Home search field (keyboard search action)
  - Then matching titles from all categories are listed, or a "no results" state is shown when none match.
  - Search does not run while the user is still typing. Back from the results returns to Home with the
    query kept in the field.
- **AC-7** Title details content
  - Given Title details is open and data is available
  - Then it shows artwork, name, release year, rating, runtime, synopsis, main cast, genres, and
    recommendations.
- **AC-8** Favorite
  - Given Title details is open
  - When the user taps the favorite control
  - Then the title's favorite state toggles, is saved on the device, and is still the same after the app
    is restarted.
- **AC-9** Trailer
  - Given the title has a YouTube trailer
  - When the user taps "Assistir trailer"
  - Then the trailer opens in the YouTube app or the browser.
  - Given the title has no trailer, then the "Assistir trailer" action is not shown.
- **AC-10** Language
  - Given the device (or the app's per-app language setting) is Portuguese
  - Then UI text and TMDB content (titles, synopses, genres) are in Portuguese (Brazil); for any other
    language they are in English.
- **AC-11** Cache then refresh
  - Given content was loaded before
  - When the user opens Home or a Title details screen while online
  - Then the cached content is shown immediately and replaced by fresh content when it arrives.
- **AC-12** Profile entry point
  - Given Home is shown
  - Then the profile icon is visible and tapping it does nothing.
- **AC-13** Recommendations stack
  - Given Title details for title A is open
  - When the user taps recommendation B
  - Then Title details for B opens on top of A; back returns to A, and back again returns to where A was
    opened from.

## Edge cases
- **EC-1** Offline with cached content -> cached Home lists or Title details are shown with a notice that
  content may be outdated. The notice appears only on Home and Title details, and goes away once fresh
  content loads.
- **EC-2** Offline, opening a title never opened before -> offline error with retry; back still works.
- **EC-3** Offline search -> offline message with retry; no local search.
- **EC-4** Refresh fails while cached content is shown -> content stays; a non-blocking error is shown.
- **EC-5** Missing field (rating, runtime, synopsis, cast, genres, recommendations) -> that field or
  section is hidden; missing poster or artwork -> placeholder image.
- **EC-6** A category has no titles -> empty state for that tab; other tabs are unaffected.
- **EC-7** Rotation or process death -> the selected tab, search query, and scroll position are kept.
- **EC-8** Repeated favorite taps -> the last tapped state is saved; no duplicates.
- **EC-9** Very long title name or synopsis -> text wraps or is truncated without breaking the layout.
- **EC-10** No app can open the trailer -> the user is told the trailer can't be opened; nothing crashes.

## Constraints
- Android, minSdk 28 (current project setting).
- Follow the project's existing modern Android architecture, modelled on Google's Now in Android sample.
- Data comes from The Movie Database (TMDB) API; TMDB attribution is shown as their terms require.
- The TMDB API key is never committed to the repository.
- Offline: previously loaded Home lists, opened Title details, and favorites are available without network.
- Localization: Portuguese (Brazil) and English, for both UI text and TMDB content.
- Accessibility: all controls and images have screen-reader labels; layouts work at large font sizes.
- Consistent dark theme, purple accents, readable typography; content scrolls vertically where needed.
- No analytics.

## Open questions
- [ ] Should cast members be tappable? Current assumption: no. (product, before blueprint)
- [ ] English copy for "Acessar", "Mais populares", "Assistir trailer". (design, settled in mold)
- [x] When does search run? → on submit, not while typing (auto: mold proposal; avoids a request per keystroke)
- [x] What happens when a recommendation is opened? → a new Title details on top, back returns (auto: mold proposal; matches the prototype's back action)
- [x] Where does the offline notice appear? → Home and Title details only (auto: the only screens with cached content)

## Change log
- 2026-10-28: Initial version.
- 2026-10-28: AC-6 search runs on submit; added AC-13 recommendations stack; EC-1 notice on Home and
  Title details only (from mold, auto).

## Subtasks
1. [01 - Browse Home by category](subtasks/01-home-browse.md)
2. [02 - Title details](subtasks/02-title-details.md)
3. [03 - Onboarding on first launch](subtasks/03-onboarding.md)
4. [04 - Favorite and trailer](subtasks/04-favorite-and-trailer.md)
5. [05 - Search](subtasks/05-search.md)
6. [06 - Offline cache and states](subtasks/06-offline-and-states.md)
7. [07 - Rotation, accessibility, and attribution](subtasks/07-resilience-and-accessibility.md)
