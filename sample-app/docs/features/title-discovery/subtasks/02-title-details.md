# 02 - Title details

Feature: [task.md](../task.md)
Depends on: 01-home-browse
Design: [show](../screens/show.md) (Content, No trailer is out of scope, Missing fields)
Status: Todo

## Goal
Tapping any poster on Home opens Title details with artwork, name, year, release date, rating, runtime,
synopsis, main cast, genres, and recommendations. Recommendations open their own details on top, and back
returns step by step.

## Scope
### In scope
- Title details screen for movies and TV shows, from TMDB.
- Opening details from featured and "Mais populares" posters, and from recommendations.
- Back via "Voltar" and the system back.
- Hiding missing fields; placeholder for missing artwork; long text wrapping.
- The favorite button and "Assistir trailer" are not shown yet.
### Out of scope (handled later)
- Favorite and trailer -> 04-favorite-and-trailer
- Opening details from search results -> 05-search
- Loading, error, cached states -> 06-offline-and-states

## Acceptance criteria
- **AC-5** Open details (partial: from Home and recommendations; search results in 05-search)
  - Given a title is shown on Home, in search results, or in recommendations
  - When the user taps it
  - Then Title details for that title opens, and back returns to the previous screen.
- **AC-7** Title details content
  - Given Title details is open and data is available
  - Then it shows artwork, name, release year, rating, runtime, synopsis, main cast, genres, and
    recommendations.
- **AC-13** Recommendations stack
  - Given Title details for title A is open
  - When the user taps recommendation B
  - Then Title details for B opens on top of A; back returns to A, and back again returns to where A was
    opened from.

## Edge cases
- **EC-5** Missing field (rating, runtime, synopsis, cast, genres, recommendations) -> that field or
  section is hidden; missing poster or artwork -> placeholder image.
- **EC-9** Very long title name or synopsis -> text wraps or is truncated without breaking the layout.

## Layers touched
- UI (details screen), state holder, repository, network, app navigation

## How to test
1. On Home, tap a featured poster: details open with all sections; the screen scrolls vertically.
2. Back with "Voltar" and with the system back: Home is shown.
3. Tap a "Mais populares" poster, then a recommendation, then another: each opens on top; back walks back
   through each title to Home.
4. Open a TV series: runtime and release date come from the series' episodes and first air date.
5. Open a little-known title with no synopsis or cast: those sections are hidden.
