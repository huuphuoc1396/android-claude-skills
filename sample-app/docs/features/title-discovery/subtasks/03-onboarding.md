# 03 - Onboarding on first launch

Feature: [task.md](../task.md)
Depends on: 01-home-browse
Design: [onboard](../screens/onboard.md) (Default)
Status: Done

## Goal
On first launch the user sees Onboarding and enters Home with "Acessar"; on every later launch the app
opens on Home directly.

## Scope
### In scope
- Onboarding screen with artwork, logo, headline, body, and "Acessar".
- Remembering on the device that onboarding was completed.
- Onboarding removed from the back stack after "Acessar".
- Using the exported artwork and wordmark as placeholders until the designer supplies final assets.
### Out of scope (handled later)
- Final licensed artwork and official TMDB logo (designer, needs-export in design.md)

## Acceptance criteria
- **AC-1** Onboarding on first launch only
  - Given the app is launched for the first time
  - When the user taps "Acessar"
  - Then Home opens, and on every later launch the app opens directly on Home.

## Edge cases
- none in this subtask

## Layers touched
- UI (onboarding screen), state holder, local preferences, app navigation

## How to test
1. Clear app data and launch: Onboarding shows.
2. Tap "Acessar": Home opens; system back exits the app instead of returning to Onboarding.
3. Kill and relaunch: Home opens directly.
