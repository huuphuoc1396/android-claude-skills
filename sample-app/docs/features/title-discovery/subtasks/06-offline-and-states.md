# 06 - Offline cache and states

Feature: [task.md](../task.md)
Depends on: 02-title-details
Design: [home](../screens/home.md) (Loading, Empty, Error, Cached notice, Refresh failed), [show](../screens/show.md) (Loading, Error, Cached notice)
Status: Done

## Goal
Home lists and opened Title details are stored on the device, shown instantly on the next visit, and
refreshed from the network. Offline, cached content shows with a notice; otherwise the user gets proper
loading, empty, and error states with retry.

## Scope
### In scope
- Storing Home lists per category and opened Title details on the device.
- Cache first, then refresh on every visit.
- Loading, empty, error-with-retry, cached-notice, and refresh-failed states on Home and Title details.
### Out of scope (handled later)
- Pre-downloading details for unopened titles (out of scope for the feature)

## Acceptance criteria
- **AC-11** Cache then refresh
  - Given content was loaded before
  - When the user opens Home or a Title details screen while online
  - Then the cached content is shown immediately and replaced by fresh content when it arrives.

## Edge cases
- **EC-1** Offline with cached content -> cached Home lists or Title details are shown with a notice that
  content may be outdated. The notice appears only on Home and Title details, and goes away once fresh
  content loads.
- **EC-2** Offline, opening a title never opened before -> offline error with retry; back still works.
- **EC-4** Refresh fails while cached content is shown -> content stays; a non-blocking error is shown.
- **EC-6** A category has no titles -> empty state for that tab; other tabs are unaffected.

## Layers touched
- UI (Home and details states), state holders, repository, local database

## How to test
1. Open Home and a few titles online. Turn on airplane mode, kill and relaunch: Home and those titles show
   with the offline notice.
2. Offline, open a title never opened before: offline error with retry; back works.
3. Back online, tap retry: the title loads; the notice disappears on the next refresh.
4. Clear app data and launch offline: Home shows the error state with retry.
