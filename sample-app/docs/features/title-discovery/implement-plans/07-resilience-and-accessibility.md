# Plan: Rotation, accessibility, and attribution

Requirement: [07 - Rotation, accessibility, and attribution](../subtasks/07-resilience-and-accessibility.md)
Status: Done

## Summary
Keep the selected tab and the edited search query in `SavedStateHandle` (scroll positions already use
saveable Compose state), let fixed-height controls grow with large fonts, add screen-reader semantics
where controls aren't announced well, and credit TMDB in a footer on Home.

## Decisions
- Selected category saved under `category` in Home's `SavedStateHandle`; the Search query under `query` in Search's (auto) — survives rotation and process death (EC-7); the Home query already does.
- Scroll positions rely on `rememberLazyListState`, `rememberLazyGridState`, and `rememberScrollState`, which are already saveable (auto) — no extra code; rejected: hoisting scroll state into ViewModels.
- Search field, tabs, and primary button use `heightIn(min = …)` instead of a fixed height (auto) — text can't clip at large font sizes.
- Tabs get `Role.Tab` and `selected` semantics; the rating ring gets a content description ("Rating 45%" / "Avaliação 45%"); the search field gets the placeholder as its content description (auto).
- TMDB attribution: a footer line on Home, "This product uses the TMDB API but is not endorsed or certified by TMDB." (pt: "Este produto usa a API do TMDB, mas não é endossado nem certificado pelo TMDB.") (auto) — TMDB's terms require this notice; the official logo waits for the designer (design.md needs-export).

## Reuse
| Need | Existing component | Location |
|---|---|---|
| Saved query pattern | `HomeViewModel.query` via `SavedStateHandle.getStateFlow` | feature/home |
| Text colors | `SampleColors.TextTertiary` | core/designsystem |

## Changes overview
| Module | Files (new / modified) | Why |
|---|---|---|
| core:designsystem | `SampleSearchField.kt`, `SamplePrimaryButton.kt` | min heights, semantics |
| feature:home | `HomeViewModel.kt`, `HomeScreen.kt`, strings, test | saved tab, tab semantics, footer |
| feature:search | `SearchViewModel.kt`, test | saved query |
| feature:details | `DetailsScreen.kt`, strings | rating description, cast name ellipsis |

## Steps
### Step 1: Saved tab and query  (EC-7) ✅
- Files: `HomeViewModel.kt` (`selectedCategory = savedStateHandle.getStateFlow("category", Category.Films)`; `selectCategory` writes it; the first refresh uses the restored value), `SearchViewModel.kt` (`query = savedStateHandle.getStateFlow("query", …)`; `onQueryChange` writes it; `submittedQuery` starts from it), tests.
- Unit tests: `HomeViewModelTest.selectedCategory_restoredFromSavedState`; `SearchViewModelTest.editedQuery_isSavedInState`.
- Compile: `./gradlew :feature:home:compileDebugUnitTestKotlin :feature:search:compileDebugUnitTestKotlin`

### Step 2: Accessibility and large fonts  (constraint: accessibility) ✅
- Files: `SampleSearchField.kt`, `SamplePrimaryButton.kt`, `HomeScreen.kt` (tabs semantics), `DetailsScreen.kt` (rating ring description, cast name `TextOverflow.Ellipsis`), details strings (`feature_details_rating` "Rating %1$d%%" / "Avaliação %1$d%%").
- Unit tests: none (UI semantics; manual: How to test 3–4).
- Compile: `./gradlew :core:designsystem:compileDebugKotlin :feature:home:compileDebugKotlin :feature:details:compileDebugKotlin`

### Step 3: TMDB attribution  (constraint: attribution) ✅
- Files: `HomeScreen.kt` (footer item after the rows), home strings (`feature_home_attribution`).
- Unit tests: none (manual: How to test 5).
- Compile: `./gradlew :app:assembleDebug`

Tests after the last step: `./gradlew testDebugUnitTest`

## Traceability
| AC / EC | Step(s) | Test(s) |
|---|---|---|
| EC-7 | 1 | `HomeViewModelTest.selectedCategory_restoredFromSavedState`, `HomeViewModelTest.query_restoredFromSavedState`, `SearchViewModelTest.editedQuery_isSavedInState`; manual: How to test 1–2 (scroll) |
| Accessibility | 2 | manual: How to test 3–4 |
| Attribution | 3 | manual: How to test 5 |

## Out of scope
- A landscape-specific layout (open question in design.md)

## Risks
- The attribution is text only until the official TMDB logo is exported.

## Deviations
- `SamplePrimaryButton` gained 16 × 10 dp inner padding so a taller label at large font sizes still has room.
- The tabs' semantics write `this.selected` because the composable's `selected` parameter shadows the semantics property.
