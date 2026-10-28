# 05 - Search

Feature: [task.md](../task.md)
Depends on: 02-title-details
Design: [search-results](../screens/search-results.md) (Loading, Results, No results, Offline, Error), [home](../screens/home.md) (search field)
Status: Todo

## Goal
The user submits a query in the Home search field and sees matching titles from all categories; tapping
one opens its details. Offline, the user is told search needs a connection.

## Scope
### In scope
- Active search field on Home; search runs on submit.
- Search results screen with loading, results, no results, offline, and error states.
- Opening details from a result; back to results, then to Home with the query kept.
### Out of scope (handled later)
- Searching cached titles offline (out of scope for the feature)

## Acceptance criteria
- **AC-5** Open details (completes: from search results; Home and recommendations in 02-title-details)
  - Given a title is shown on Home, in search results, or in recommendations
  - When the user taps it
  - Then Title details for that title opens, and back returns to the previous screen.
- **AC-6** Search
  - Given the user is online
  - When the user submits a query in the Home search field (keyboard search action)
  - Then matching titles from all categories are listed, or a "no results" state is shown when none match.
  - Search does not run while the user is still typing. Back from the results returns to Home with the
    query kept in the field.

## Edge cases
- **EC-3** Offline search -> offline message with retry; no local search.

## Layers touched
- UI (search results screen, Home search field), state holder, repository, network, app navigation

## How to test
1. Type "matrix" without submitting: nothing happens. Submit: results show movies and series.
2. Tap a result: details open; back returns to results; back again returns to Home with "matrix" kept.
3. Search "zzzzqqqq": the no results message shows.
4. Turn on airplane mode and search: the offline message shows; turn it off and tap retry: results load.
