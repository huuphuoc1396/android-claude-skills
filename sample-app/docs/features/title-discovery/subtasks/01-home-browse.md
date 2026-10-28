# 01 - Browse Home by category

Feature: [task.md](../task.md)
Depends on: none
Design: [home](../screens/home.md) (Content state)
Status: Done

## Goal
The app opens on Home instead of News. The user sees the heading, search field, profile avatar, the four
category tabs, a featured carousel, and "Mais populares", filled with real TMDB titles for the selected
tab, in the dark theme from the design.

## Scope
### In scope
- Remove the News feature; Home becomes the start screen.
- Dark theme with the design's colors and Inter typography.
- TMDB access with the API key kept out of the repository.
- Category tabs Filmes / Séries / Animes / Novelas with the category rules.
- Featured carousel and "Mais populares" for the selected tab, posters loaded from TMDB.
- Search field shown but not active; profile avatar shown with no action.
- UI text in pt-BR and English, and TMDB content requested in the device language.
- A plain loading indicator and a plain error message, enough for the happy path.
### Out of scope (handled later)
- Tapping a poster -> 02-title-details
- Onboarding -> 03-onboarding
- Search -> 05-search
- Caching, offline, empty, and refresh states -> 06-offline-and-states
- Rotation and process death, accessibility, attribution -> 07-resilience-and-accessibility

## Acceptance criteria
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
- **AC-10** Language
  - Given the device (or the app's per-app language setting) is Portuguese
  - Then UI text and TMDB content (titles, synopses, genres) are in Portuguese (Brazil); for any other
    language they are in English.
  - Later subtasks add their own copy in both languages under this rule.
- **AC-12** Profile entry point
  - Given Home is shown
  - Then the profile icon is visible and tapping it does nothing.

## Edge cases
- none in this subtask

## Layers touched
- UI (Home screen, theme, shared poster component), state holder, repository, network, app navigation

Size risk: about 5 modules (network, data, designsystem, a new Home feature, app), over the 3-module guide;
kept as one slice because nothing smaller shows real titles end to end.

## How to test
1. Set the TMDB key as described in the README or CLAUDE.md, then launch the app: Home opens, News is gone.
2. Filmes is selected; both rows show movie posters. Scroll each row horizontally.
3. Tap Séries, Animes, Novelas: both rows change to that category (anime are Japanese animation, novelas
   are Latin American soaps).
4. Tap the profile avatar: nothing happens.
5. Set the device language to Português (Brasil): labels and titles switch to Portuguese; English otherwise.
