# 07 - Rotation, accessibility, and attribution

Feature: [task.md](../task.md)
Depends on: 05-search, 06-offline-and-states
Design: [home](../screens/home.md), [show](../screens/show.md), [search-results](../screens/search-results.md), [onboard](../screens/onboard.md)
Status: Todo

## Goal
The app keeps the user's place through rotation and process death, works with a screen reader and large
fonts, and credits TMDB as their terms require.

## Scope
### In scope
- Keeping the selected tab, search query, and scroll positions across rotation and process death.
- Screen-reader labels for every control and image; layouts that hold up at large font sizes.
- TMDB attribution where the terms require it.
### Out of scope (handled later)
- A landscape-specific layout (open question in design.md)

## Acceptance criteria
- none new; this subtask covers edge cases and constraints

## Edge cases
- **EC-7** Rotation or process death -> the selected tab, search query, and scroll position are kept.

Constraints covered: accessibility (screen-reader labels, large fonts) and TMDB attribution.

## Layers touched
- UI (all screens), state holders

## How to test
1. On Home pick Animes and scroll; rotate: the same tab and position are shown.
2. With "Don't keep activities" on, background and return: tab, query, and scroll are kept.
3. With TalkBack on, move through Home and details: every control and poster is announced by name.
4. Set the largest font size: no text is cut off or overlapping.
5. TMDB is credited in the app.
