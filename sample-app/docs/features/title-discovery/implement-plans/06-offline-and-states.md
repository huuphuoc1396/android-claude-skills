# Plan: Offline cache and states

Requirement: [06 - Offline cache and states](../subtasks/06-offline-and-states.md)
Status: Done

## Summary
Make Home lists and Title details offline-first, the same way the removed News feature was: Room is the
source of truth, screens observe it, and a refresh on every visit writes fresh data into it. Database v3
adds `category_titles` and `title_details` and drops the unused `articles` table. Home and Details
ViewModels combine "what is cached" with "how the last refresh went" into loading, empty, offline-error,
error, cached-notice, and refresh-failed states.

## Decisions
- Two tables: `category_titles` (one row per poster per section, ordered by position) and `title_details` (scalar columns; cast, genres, recommendations as JSON through Room `TypeConverters` with kotlinx.serialization). (user; rejected: fully normalized tables)
- Database 2 → 3 auto-migration with `@DeleteTable("articles")`; `ArticleEntity` and `ArticleDao` are deleted. (user; rejected: keeping the table)
- `TitleRepository` becomes observe + refresh: `observeCategoryTitles(category): Flow<CategoryTitles>`, `refreshCategory(category)`, `observeTitleDetails(id, mediaType): Flow<TitleDetails?>`, `refreshTitleDetails(id, mediaType)`; `getCategoryTitles` / `getTitleDetails` are removed; the class is renamed `OfflineFirstTitleRepository` (auto) — matches the project's earlier offline-first repository.
- A category refresh replaces that category's rows in one transaction (auto) — no stale posters mixed with fresh ones.
- Refresh outcome: `Loading | Done | Offline | Failed`; Offline means the refresh threw an `IOException` (auto) — same rule as search.
- States, for Home per category and for Details (auto):
  - cached content: `Success`, with `offlineNotice = (refresh == Offline)` and `refreshFailed = (refresh == Failed)` (EC-1, EC-4);
  - nothing cached: `Loading` while refreshing, `Empty` after a successful empty refresh (Home only, EC-6), otherwise `Error(offline = refresh == Offline)` with retry (EC-2).
- Refresh failure with cache shows a snackbar once per failed refresh (auto) — home.md "Refresh failed".
- The notice is a one-line text under the tabs on Home and under the divider on Details (auto) — design.md proposal.

## Reuse
| Need | Existing component | Location |
|---|---|---|
| Offline-first shape | removed `OfflineFirstArticleRepository` + `NewsViewModel` refresh state (git history) | commit 8b7c04b |
| Database, DI | `SampleDatabase`, `DatabaseModule` | core/database |
| Serialization | `kotlinx-serialization-json`, `kotlin-serialization` plugin | gradle/libs.versions.toml |
| States UI | `SampleLoadingState`, `SampleEmptyState`, `SampleErrorState` | core/designsystem |

## Design mapping
| Design | Code |
|---|---|
| home Loading / Empty / Error / Cached notice / Refresh failed _(proposed)_ | `HomeContent` states; `OfflineNotice` text; `SnackbarHost` |
| show Loading / Error / Cached notice _(proposed)_ | `DetailsUiState` states; `OfflineNotice`; `SnackbarHost` (shared with trailer error) |

## Changes overview
| Module | Files (new / modified) | Why |
|---|---|---|
| core:database | build.gradle.kts; `model/CategoryTitleEntity.kt`, `model/TitleDetailsEntity.kt`, `dao/CategoryTitleDao.kt`, `dao/TitleDetailsDao.kt`, `util/Converters.kt`, `migration/DatabaseMigrations.kt` (new); `SampleDatabase.kt`, `di/DatabaseModule.kt`; delete `ArticleEntity.kt`, `ArticleDao.kt`; `schemas/…/3.json` (generated) | cache tables |
| core:data | `repository/TitleRepository.kt`, `OfflineFirstTitleRepository.kt` (renamed), `model/TitleMappers.kt`, `di/DataModule.kt`, tests and fakes | offline-first |
| core:testing | `TestTitleRepository.kt` | observe/refresh double |
| feature:home | `HomeUiState.kt`, `HomeViewModel.kt`, `HomeScreen.kt`, strings, test | states |
| feature:details | `DetailsUiState.kt`, `DetailsViewModel.kt`, `DetailsScreen.kt`, strings, test | states |

## Steps
### Step 1: Cache tables  (AC-11) ✅
- Files: as in Changes overview for core:database.
- What: `CategoryTitleEntity(category, section, position, id, mediaType, name, posterUrl?)` PK `(category, section, position)`; `TitleDetailsEntity(id, mediaType, name, releaseDate?, releaseCountry?, ratingPercent?, runtimeMinutes?, synopsis?, artworkUrl?, trailerUrl?, cast: List<CachedCastMember>, genres: List<String>, recommendations: List<CachedTitle>)` PK `(id, media_type)`; `@Serializable CachedCastMember(name, photoUrl?)`, `CachedTitle(id, mediaType, name, posterUrl?)`; `Converters` for the three list types; `CategoryTitleDao.observe(category)`, `@Transaction replaceCategory(category, rows)`; `TitleDetailsDao.observe(id, mediaType): Flow<TitleDetailsEntity?>`, `upsert`; `version = 3`, `AutoMigration(2, 3, spec = DatabaseMigrations.Schema2to3::class)`.
- Unit tests: `ConvertersTest.castRoundTrip`, `recommendationsRoundTrip`, `genresRoundTrip`.
- Compile: `./gradlew :core:database:compileDebugKotlin :core:database:compileDebugUnitTestKotlin`

### Step 2: Offline-first repository  (AC-11) ✅
- Files: core:data as in Changes overview; `core/testing/.../TestTitleRepository.kt`.
- What: entity ↔ model mappers; `observeCategoryTitles` groups rows by section; `refreshCategory` fetches as today and calls `replaceCategory`; `observeTitleDetails` maps or null; `refreshTitleDetails` fetches and upserts. `TestTitleRepository`: `MutableStateFlow` per category and per details key, `refreshError: Exception?`, `sendCategoryTitles`, `sendTitleDetails`, refresh counters.
- Unit tests: `OfflineFirstTitleRepositoryTest` (renamed): existing category/details/search tests rewritten as refresh-then-observe, plus `refreshCategory_replacesOldRows`, `observeTitleDetails_beforeRefresh_isNull`, `refresh_whenNetworkFails_throwsAndKeepsCache`.
- Compile: `./gradlew :core:data:compileDebugUnitTestKotlin :core:testing:compileDebugKotlin`

### Step 3: Home states  (AC-11, EC-1, EC-4, EC-6) ✅
- Files: `HomeUiState.kt` (`HomeContent`: `Loading`, `Empty`, `Error(offline: Boolean)`, `Success(featured, popular, offlineNotice: Boolean, refreshFailed: Boolean)`), `HomeViewModel.kt` (per category: `combine(observeCategoryTitles, refreshState)`; refresh on category change and retry), `HomeScreen.kt` (empty and offline messages, notice line, snackbar), strings, `HomeViewModelTest.kt`.
- Strings (en / pt): `feature_home_empty` "Nothing here yet"/"Nada por aqui ainda", `feature_home_offline` "You're offline"/"Você está offline", `feature_home_cached_notice` "You're offline. Content may be out of date."/"Você está offline. O conteúdo pode estar desatualizado."
- Unit tests: `HomeViewModelTest`: `cachedContent_shownWhileRefreshing`, `refreshOffline_withCache_showsNotice`, `refreshFailed_withCache_flagsRefreshFailed`, `emptyAfterRefresh_showsEmpty`, `offline_withoutCache_showsOfflineError`, `retry_afterError_recovers`, `selectCategory_refreshesThatCategory`, plus the query tests.
- Compile: `./gradlew :feature:home:compileDebugUnitTestKotlin`

### Step 4: Details states  (AC-11, EC-1, EC-2, EC-4) ✅
- Files: `DetailsUiState.kt` (`Loading`, `Error(offline)`, `Success(details, isFavorite, offlineNotice, refreshFailed)`), `DetailsViewModel.kt`, `DetailsScreen.kt` (offline error message, notice under the divider, refresh-failed snackbar), strings, `DetailsViewModelTest.kt`.
- Strings (en / pt): `feature_details_offline`, `feature_details_cached_notice`, `feature_details_refresh_failed` "Couldn't refresh"/"Não foi possível atualizar".
- Unit tests: `DetailsViewModelTest`: `cachedDetails_shownWhileRefreshing`, `offline_withoutCache_showsOfflineError`, `refreshOffline_withCache_showsNotice`, `refreshFailed_withCache_flagsRefreshFailed`, `retry_afterError_recovers`, plus the favorite tests.
- Compile: `./gradlew :feature:details:compileDebugUnitTestKotlin :app:assembleDebug`

Tests after the last step: `./gradlew testDebugUnitTest`

## Traceability
| AC / EC | Step(s) | Test(s) |
|---|---|---|
| AC-11 | 1–4 | `OfflineFirstTitleRepositoryTest.*`, `HomeViewModelTest.cachedContent_shownWhileRefreshing`, `DetailsViewModelTest.cachedDetails_shownWhileRefreshing` |
| EC-1 | 3, 4 | `*.refreshOffline_withCache_showsNotice` |
| EC-2 | 4 | `DetailsViewModelTest.offline_withoutCache_showsOfflineError` |
| EC-4 | 3, 4 | `*.refreshFailed_withCache_flagsRefreshFailed`; manual: snackbar |
| EC-6 | 3 | `HomeViewModelTest.emptyAfterRefresh_showsEmpty` |

## Out of scope
- Pre-downloading details for unopened titles (out of scope for the feature)

## Risks
- The 2 → 3 auto-migration (two new tables, one dropped) is only verified by building; no instrumented migration test.
- Cached image files depend on Coil's disk cache; offline posters for titles never displayed won't load.

## Deviations
- `TestTitleRepository` emits empty `CategoryTitles` and null details until data is sent, matching the real cache, so ViewModel tests see the same first emission as the app.
- Existing repository tests kept their names and read through private `getCategoryTitles` / `getTitleDetails` helpers (refresh, then observe) instead of being rewritten one by one.
- The Home refresh-failed snackbar reuses `feature_home_error` ("Couldn't load"); Details has its own "Couldn't refresh".
