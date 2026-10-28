# Plan: Search

Requirement: [05 - Search](../subtasks/05-search.md)
Status: Done

## Summary
Add TMDB `search/multi` to the network and `TitleRepository`, move Home's search field into the design
system as an editable `SampleSearchField`, and add a new `:feature:search` module. Home keeps the typed
query in its `SavedStateHandle` and opens Search results on submit; the results screen can edit and
resubmit, and tells "offline" apart from other errors.

## Decisions
- New `:feature:search` module. (user; rejected: inside :feature:home)
- `search/multi` with `include_adult=false`, keeping only `movie` and `tv` results (people are dropped) (auto) — one call covers all categories (AC-6).
- Offline = the call threw an `IOException`; any other failure is a generic error (auto) — Retrofit throws `IOException` for no connection and `HttpException` for server errors.
- Search runs only on the keyboard's search action; blank queries are ignored (auto) — AC-6.
- Home keeps its query in `SavedStateHandle` under `query` (auto) — Home stays on the back stack, so back from results shows the same text (AC-6), and it survives process death (EC-7, finished in 07).
- Results are a grid with `GridCells.Adaptive(124.dp)` of `SamplePoster` (auto) — search-results.md proposes a poster grid; adaptive keeps posters at design size on any width.
- `SampleSearchField(query, onQueryChange, onSearch, placeholder, modifier)` and `ic_search` move to `core:designsystem` (auto) — used by Home and Search.

## Reuse
| Need | Existing component | Location |
|---|---|---|
| TMDB client | `TmdbApi`, `RetrofitTmdbNetwork` | core/network |
| Title mapping | `NetworkTitle.asExternalModel` | core/data/.../TitleMappers.kt |
| Search field look | Home's `HomeSearchField` → `SampleSearchField` | feature/home → core/designsystem |
| Poster | `SamplePoster` | core/designsystem |
| Back button look | details `BackButton` (icon + label pattern) | feature/details |
| States | `SampleLoadingState`, `SampleErrorState`, `SampleEmptyState` | core/designsystem |

## Design mapping
| Design | Code |
|---|---|
| search-results screen _(proposed)_ | `SearchRoute` / `SearchScreen`, `SearchDestination(query)` |
| Search field (home.md) | `SampleSearchField` |
| Poster grid | `LazyVerticalGrid(GridCells.Adaptive(124.dp))` |
| Loading / No results / Offline / Error | `SampleLoadingState`, `SampleEmptyState`, `SampleErrorState` |
| Nav: home submit → search-results; result → show; back → home with query | `navigate(SearchDestination(query))`; `navigateToDetails`; `popBackStack()` |

## Assets
| Asset | Status | Action | Destination | Step |
|---|---|---|---|---|
| ic_search | provided | move from feature/home | core/designsystem/src/main/res/drawable/ic_search.xml | 3 |
| ic_back | provided | copy the details VectorDrawable | feature/search/src/main/res/drawable/ic_back.xml | 4 |

## Changes overview
| Module | Files (new / modified) | Why |
|---|---|---|
| core:network | `TmdbNetworkDataSource.kt`, `RetrofitTmdbNetwork.kt` | search endpoint |
| core:data | `TitleRepository.kt`, `DefaultTitleRepository.kt`, `TitleMappers.kt`, tests | search |
| core:testing | `TestTitleRepository.kt` | search support |
| core:designsystem | `component/SampleSearchField.kt` (new), `res/drawable/ic_search.xml` (moved) | shared field |
| feature:home | `HomeViewModel.kt`, `HomeScreen.kt`, `navigation/HomeNavigation.kt`, test | editable field, submit |
| feature:search | new module | results screen |
| app | `build.gradle.kts`, `SampleNavHost.kt`; settings.gradle.kts | wiring |

## Steps
### Step 1: Search endpoint  (AC-6) ✅
- Files: `TmdbNetworkDataSource.kt` (`suspend fun search(query: String): List<NetworkTitle>`), `RetrofitTmdbNetwork.kt` (`@GET("search/multi") search(@Query("query") query, @Query("include_adult") includeAdult = false): NetworkPage`), data `FakeTmdbNetworkDataSource`
- Unit tests: none here (covered in step 2).
- Compile: `./gradlew :core:network:compileDebugKotlin :core:data:compileDebugUnitTestKotlin`

### Step 2: TitleRepository.search  (AC-6, EC-3) ✅
- Files: `TitleRepository.kt` (`suspend fun search(query: String): List<Title>` — throws `IOException` when offline), `DefaultTitleRepository.kt`, `TitleMappers.kt` (`String.toTmdbMediaType()` made internal), `TestTitleRepository.kt` (`searchResults: Map<String, List<Title>>`, `searchError: Exception?`), `DefaultTitleRepositoryTest.kt`
- Unit tests: `search_keepsOnlyMoviesAndTv`, `search_passesQuery`.
- Compile: `./gradlew :core:data:compileDebugUnitTestKotlin :core:testing:compileDebugKotlin`

### Step 3: Editable search field on Home  (AC-6) ✅
- Files: `core/designsystem/.../component/SampleSearchField.kt` (new), `ic_search.xml` (move), `feature/home` `HomeViewModel.kt` (`SavedStateHandle`; `val query: StateFlow<String>`; `fun onQueryChange(String)`), `HomeScreen.kt` (`SampleSearchField`; `onSearch(query)` when not blank), `navigation/HomeNavigation.kt` (`homeScreen(onTitleClick, onSearch: (String) -> Unit)`), `HomeViewModelTest.kt`
- What: `SampleSearchField` = the Home field look with a `BasicTextField` (single line, `ImeAction.Search`, `onSearch` on the action), placeholder shown when empty.
- Unit tests: `HomeViewModelTest.onQueryChange_updatesQuery`, `query_restoredFromSavedState`.
- Compile: `./gradlew :feature:home:compileDebugUnitTestKotlin`

### Step 4: Search feature  (AC-5, AC-6, EC-3) ✅
- Files: `settings.gradle.kts`, `feature/search/build.gradle.kts`, `SearchUiState.kt`, `SearchViewModel.kt`, `SearchScreen.kt`, `navigation/SearchNavigation.kt`, `res/values(-pt)/strings.xml`, `res/drawable/ic_back.xml` (new); `app/build.gradle.kts`, `SampleNavHost.kt`
- What:
  - `sealed interface SearchResults { Loading; Offline; Error; NoResults; data class Found(titles) }`; `SearchUiState(query: String, results: SearchResults)`.
  - `SearchViewModel(savedStateHandle, titleRepository)`: `query` starts from the route; `submittedQuery` drives `flatMapLatest { emit Loading; emit search }`; `onQueryChange`, `submit()`, `retry()`.
  - `SearchDestination(query: String)`; `searchScreen(onBack, onTitleClick)`; `NavController.navigateToSearch(query)`.
  - Screen: status-bar padding, back icon + `SampleSearchField` row, grid of posters with `onTitleClick`.
  - Strings (en / pt): `feature_search_hint` "Search"/"Buscar", `feature_search_back` "Back"/"Voltar", `feature_search_no_results` "No results for \"%1$s\""/"Nenhum resultado para \"%1$s\"", `feature_search_offline` "You're offline"/"Você está offline", `feature_search_error` "Couldn't load"/"Não foi possível carregar", `feature_search_retry` "Try again"/"Tentar novamente".
- Unit tests: `SearchViewModelTest.initialQuery_showsResults`, `noMatches_showsNoResults`, `ioException_showsOffline`, `otherException_showsError`, `submit_newQuery_searchesAgain`, `retry_afterOffline_recovers`.
- Compile: `./gradlew :feature:search:compileDebugUnitTestKotlin :app:assembleDebug`

Tests after the last step: `./gradlew testDebugUnitTest`

## Traceability
| AC / EC | Step(s) | Test(s) |
|---|---|---|
| AC-5 (completes) | 4 | manual: How to test 2 |
| AC-6 | 1–4 | `DefaultTitleRepositoryTest.search_*`, `SearchViewModelTest.initialQuery_…`, `noMatches_…`, `submit_…`, `HomeViewModelTest.onQueryChange_…`; manual: How to test 1–3 |
| EC-3 | 2, 4 | `SearchViewModelTest.ioException_showsOffline`, `retry_afterOffline_recovers` |

## Out of scope
- Searching cached titles offline (out of scope for the feature)

## Risks
- `search/multi` returns people too; they are dropped, so a page can show fewer than 20 results.

## Deviations
- `TitleRepository.kt` needed a `Title` import (compile fix).
- The search results back control is an icon with a content description (no "Voltar" label), since the search field fills the row.
