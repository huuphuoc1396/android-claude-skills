# 04 - Favorite and trailer

Feature: [task.md](../task.md)
Depends on: 02-title-details
Design: [show](../screens/show.md) (Favorited, No trailer, Trailer can't open)
Status: Done

## Goal
On Title details the user can favorite or unfavorite a title, and it stays that way after a restart. When
a YouTube trailer exists, "Assistir trailer" opens it outside the app; otherwise the button is hidden.

## Scope
### In scope
- Favorite button with outline and filled states, saved on the device.
- "Assistir trailer" button, shown only when a YouTube trailer exists.
- Opening the trailer in the YouTube app or the browser, with a message if nothing can open it.
### Out of scope (handled later)
- A favorites list (out of scope for the feature)

## Acceptance criteria
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

## Edge cases
- **EC-8** Repeated favorite taps -> the last tapped state is saved; no duplicates.
- **EC-10** No app can open the trailer -> the user is told the trailer can't be opened; nothing crashes.

## Layers touched
- UI (details screen), state holder, repository, local database, network

## How to test
1. Open a title, tap the heart: it fills pink. Kill and relaunch, open the same title: still filled.
2. Tap the heart quickly several times: the final state is kept after relaunch.
3. Tap "Assistir trailer": YouTube (or the browser) opens the trailer; back returns to details.
4. Open a title without a trailer: the button is not shown.
