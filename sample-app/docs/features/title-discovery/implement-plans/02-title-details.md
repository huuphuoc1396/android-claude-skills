# Plan: Title details

Requirement: [02 - Title details](../subtasks/02-title-details.md)
Status: Done

## Summary
Add TMDB movie and TV detail calls (credits and recommendations appended in one request), a
`TitleDetails` model built by `TitleRepository`, and a new `:feature:details` module that renders the Show
design. Home posters and recommendations navigate to it with a type-safe route; each opens a new entry on
the back stack, so back walks through them (AC-13). Network-only, like Home; loading and error are plain
until subtask 06.

## Decisions
- New `:feature:details` module. (user; rejected: inside :feature:home)
- One request per title: `movie/{id}` or `tv/{id}` with `append_to_response=credits,recommendations` (+ `release_dates` for movies) (auto) — one round trip; rejected: separate credits/recommendations calls.
- Release country: the regional theatrical release for `BR` when the app language is Portuguese, `US` otherwise; falls back to `release_date` with no country. TV uses `first_air_date` and the first origin country (auto) — matches "10/12/2021 (BR)".
- The region rule lives next to the language rule in `core:network` and is exposed as `TmdbNetworkDataSource.region` (auto) — one place decides locale policy.
- Rating = `vote_average × 10` rounded, as a percentage; hidden when `vote_count` is 0 (auto) — EC-5.
- Runtime: movie `runtime`; TV first `episode_run_time`; hidden when missing or 0 (auto) — EC-5.
- Artwork is the poster at `w780`, cropped to the 493 dp header (auto) — the design's art is the poster (title text in the image); backdrops are landscape.
- Main cast: first 10 by billing order, photo at `w185` (auto).
- Recommendations: TMDB's list, up to 20, posters as on Home (auto).
- Route `DetailsDestination(id: Long, mediaType: String)`; the ViewModel reads both from `SavedStateHandle` by key (auto) — testable without Robolectric; rejected: `toRoute()` (needs Android in tests).
- Date shown as `dd/MM/yyyy` in Portuguese and `MM/dd/yyyy` otherwise; runtime as `1h 53m` (`53m` under an hour) (auto) — design.md.
- `:feature:details` uses Coil directly for the round cast photos and the header art (auto) — `SamplePoster` covers only the 124 × 188 poster.
- Favorite button and "Assistir trailer" are not drawn until 04 (auto) — subtask scope.

## Reuse
| Need | Existing component | Location |
|---|---|---|
| TMDB Retrofit + interceptor | `TmdbApi`, `TmdbRequestInterceptor` | core/network/.../retrofit |
| Locale rule | `tmdbLanguage()` | core/network/.../TmdbRequestInterceptor.kt |
| Repository | `TitleRepository`, `DefaultTitleRepository` | core/data/.../repository |
| Poster row item | `SamplePoster` | core/designsystem/.../component/SamplePoster.kt |
| Colors, gradients, type | `SampleColors`, `SampleGradients`, `SampleTheme` | core/designsystem/.../theme |
| Loading / error | `SampleLoadingState`, `SampleErrorState` | core/designsystem/.../SampleStates.kt |
| ViewModel shape, tests | `HomeViewModel`, `TestTitleRepository`, `MainDispatcherRule` | feature/home, core/testing |

## Design mapping
| Design | Code |
|---|---|
| show screen | `DetailsRoute` / `DetailsScreen`, `DetailsDestination` |
| Backdrop + fade (#21222E 70% → transparent 81%) | header `Box` with `AsyncImage` + vertical gradient; color `SampleColors.HeaderFade` (new) |
| Back ("Voltar") | `BackButton` (private) with `ic_back` |
| Rating ring | `RatingRing` (private, Canvas): track `colorScheme.surface`, arc `colorScheme.secondary`, inner `SampleColors.SurfaceDeep` |
| Title "<name> (<year>)" Inter 700 22 | `typography.headlineSmall` with 22 sp |
| Meta line, text tertiary | `typography.bodyLarge`, `SampleColors.TextTertiary`, `ic_clock` |
| Divider | `HorizontalDivider` 1 dp, black |
| Synopsis Inter 400 14 #CCCCCC | `typography.bodySmall` with 14 sp / 17.5 sp, `onSurfaceVariant` |
| Section heading | `typography.titleMedium` |
| Cast item | `CastItem` (private) |
| Genre chip | `GenreChip` (private) in a `FlowRow` |
| Recommendations | `LazyRow` of `SamplePoster` |
| Nav: home poster → show; show recommendation → show; back | `navController.navigate(DetailsDestination(...))`; `popBackStack()` |

## Assets
| Asset | Status | Action | Destination | Step |
|---|---|---|---|---|
| ic_back | provided | VectorDrawable from `assets/ic_back.svg` | feature/details/src/main/res/drawable/ic_back.xml | 3 |
| ic_clock | provided | VectorDrawable from `assets/ic_clock.svg` | feature/details/src/main/res/drawable/ic_clock.xml | 3 |

## Changes overview
| Module | Files (new / modified) | Why |
|---|---|---|
| core:network | TmdbNetworkDataSource.kt, model/NetworkTitleDetails.kt (new), retrofit/RetrofitTmdbNetwork.kt, retrofit/TmdbRequestInterceptor.kt | detail endpoints, region |
| core:data | model/TitleDetails.kt (new), model/TitleMappers.kt, repository/TitleRepository.kt, DefaultTitleRepository.kt, test doubles + tests | details model |
| core:testing | TestTitleRepository.kt | details support |
| core:designsystem | theme/Color.kt | header fade color |
| feature:details | new module | screen |
| feature:home | HomeScreen.kt, navigation/HomeNavigation.kt | poster taps |
| app | build.gradle.kts, SampleNavHost.kt; settings.gradle.kts | wiring |

## Steps
### Step 1: Detail endpoints  (AC-7) ✅
- Files: `TmdbNetworkDataSource.kt`, `model/NetworkTitleDetails.kt` (new), `retrofit/RetrofitTmdbNetwork.kt`, `retrofit/TmdbRequestInterceptor.kt`
- What:
  - `TmdbNetworkDataSource`: `val region: String`; `suspend fun getDetails(mediaType: TmdbMediaType, id: Long): NetworkTitleDetails`.
  - `NetworkTitleDetails(id, title?, name?, posterPath?, overview?, voteAverage, voteCount, runtime?, episodeRunTime: List<Int>?, releaseDate?, firstAirDate?, originCountry: List<String>?, genres: List<NetworkGenre>?, credits: NetworkCredits?, recommendations: NetworkPage?, releaseDates: NetworkReleaseDates?)`; `NetworkGenre(name)`, `NetworkCredits(cast: List<NetworkCast>)`, `NetworkCast(name, profilePath?, order)`, `NetworkReleaseDates(results: List<NetworkCountryReleases>)`, `NetworkCountryReleases(iso31661, releaseDates: List<NetworkReleaseDate>)`, `NetworkReleaseDate(releaseDate: String, type: Int)`.
  - `TmdbApi.getDetails(@Path media, @Path id, @Query("append_to_response") append)`; movies append `credits,recommendations,release_dates`, TV `credits,recommendations`.
  - `internal fun tmdbRegion(locale: Locale) = if (locale.language == "pt") "BR" else "US"`; `RetrofitTmdbNetwork.region` = `tmdbRegion(Locale.getDefault())`.
- Unit tests: `TmdbRequestInterceptorTest.tmdbRegion_portuguese_isBr`, `tmdbRegion_other_isUs`.
- Compile: `./gradlew :core:network:compileDebugKotlin :core:network:compileDebugUnitTestKotlin :core:data:compileDebugUnitTestKotlin`

### Step 2: TitleDetails in the repository  (AC-7, EC-5) ✅
- Files: `model/TitleDetails.kt` (new), `model/TitleMappers.kt`, `repository/TitleRepository.kt`, `repository/DefaultTitleRepository.kt`, `core/testing/.../TestTitleRepository.kt`, `testdoubles/FakeTmdbNetworkDataSource.kt`, `DefaultTitleRepositoryTest.kt`
- What:
  - `TitleDetails(id, mediaType, name, releaseDate: LocalDate?, releaseCountry: String?, ratingPercent: Int?, runtimeMinutes: Int?, synopsis: String?, artworkUrl: String?, cast: List<CastMember>, genres: List<String>, recommendations: List<Title>)`; `CastMember(name, photoUrl: String?)`.
  - `TitleRepository.getTitleDetails(id: Long, mediaType: MediaType): TitleDetails`.
  - Mapping `NetworkTitleDetails.asExternalModel(mediaType, region)`:
    - movie date/country: `releaseDates` entry for `region`, first `type == 3` else first date → `(date, region)`; else `releaseDate` with no country. TV: `firstAirDate`, `originCountry.firstOrNull()`. Dates parsed from the first 10 chars; blank → null.
    - rating: `voteCount > 0` → `(voteAverage * 10).roundToInt()`; runtime: `runtime ?: episodeRunTime.firstOrNull()`, `0` → null; synopsis blank → null.
    - artwork `w780`, cast photos `w185`, cast sorted by `order`, first 10; recommendations mapped with their own media type (`media_type` field on `NetworkTitle`, default the parent's), max 20.
  - `NetworkTitle` gains `@SerializedName("media_type") val mediaType: String? = null`.
- Unit tests: `DefaultTitleRepositoryTest.movieDetails_mapsAllFields`, `movieDetails_usesRegionalTheatricalRelease`, `movieDetails_withoutRegion_usesReleaseDateWithoutCountry`, `tvDetails_usesFirstAirDateAndEpisodeRuntime`, `details_missingFields_areNull`, `details_castSortedAndLimitedToTen`.
- Compile: `./gradlew :core:data:compileDebugKotlin :core:data:compileDebugUnitTestKotlin :core:testing:compileDebugKotlin :feature:home:compileDebugUnitTestKotlin`

### Step 3: Details feature  (AC-5, AC-7, AC-13, EC-5, EC-9) ✅
- Files: `settings.gradle.kts`, `feature/details/build.gradle.kts`, `DetailsUiState.kt`, `DetailsViewModel.kt`, `DetailsScreen.kt`, `DetailsFormat.kt`, `navigation/DetailsNavigation.kt`, `res/values/strings.xml`, `res/values-pt/strings.xml`, `res/drawable/ic_back.xml`, `res/drawable/ic_clock.xml` (new); `core/designsystem/.../theme/Color.kt` (`HeaderFade`)
- What:
  - `sealed interface DetailsUiState { Loading; Error; data class Success(details: TitleDetails) }`.
  - `DetailsViewModel(savedStateHandle, titleRepository)`: id = `savedStateHandle["id"]`, mediaType = `MediaType.valueOf(savedStateHandle["mediaType"])`; same load/retry flow as `HomeViewModel`.
  - `DetailsDestination(id: Long, mediaType: String)`; `NavGraphBuilder.detailsScreen(onBack: () -> Unit, onTitleClick: (Title) -> Unit)`; `fun NavController.navigateToDetails(title: Title)`.
  - `DetailsFormat.kt`: `internal fun formatRuntime(minutes: Int): String`, `internal fun formatReleaseDate(date: LocalDate, locale: Locale): String`.
  - `DetailsScreen`: background gradient, `Column(verticalScroll)`; header 493 dp art + fade, `BackButton` (statusBars + 18/36 offsets); title block overlapping the header bottom by 117 dp; then divider, synopsis, cast, genres, recommendations per show.md spacing, each section hidden when empty/null; bottom navigation-bar padding.
  - Strings (en / pt): `feature_details_back` "Back"/"Voltar", `feature_details_cast` "Main cast"/"Elenco principal", `feature_details_genres` "Genres"/"Categoria(s)", `feature_details_recommendations` "Recommendations"/"Recomendações", `feature_details_error` "Couldn't load"/"Não foi possível carregar", `feature_details_retry` "Try again"/"Tentar novamente".
- Unit tests: `DetailsViewModelTest.load_readsRouteAndShowsDetails`, `load_failure_showsError`, `retry_afterError_recovers`; `DetailsFormatTest.formatRuntime_hoursAndMinutes`, `formatRuntime_underAnHour`, `formatReleaseDate_portuguese`, `formatReleaseDate_english`.
- Compile: `./gradlew :feature:details:compileDebugKotlin :feature:details:compileDebugUnitTestKotlin`

### Step 4: Navigation from Home and recommendations  (AC-5, AC-13) ✅
- Files: `feature/home/.../HomeScreen.kt`, `navigation/HomeNavigation.kt`, `app/build.gradle.kts`, `app/.../navigation/SampleNavHost.kt`
- What: `homeScreen(onTitleClick: (Title) -> Unit)`; `HomeScreen` passes `onTitleClick` to every `SamplePoster`; NavHost registers `detailsScreen(onBack = navController::popBackStack, onTitleClick = navController::navigateToDetails)` and home's `onTitleClick = navController::navigateToDetails`.
- Unit tests: none (wiring; manual How to test 1–3).
- Compile: `./gradlew :feature:home:compileDebugKotlin :app:assembleDebug`

Tests after the last step: `./gradlew testDebugUnitTest`

## Traceability
| AC / EC | Step(s) | Test(s) |
|---|---|---|
| AC-5 (partial) | 4 | manual: How to test 1, 3 |
| AC-7 | 1, 2, 3 | `DefaultTitleRepositoryTest.movieDetails_mapsAllFields`, `tvDetails_…`; `DetailsViewModelTest.load_readsRouteAndShowsDetails` |
| AC-13 | 3, 4 | manual: How to test 3 (new back-stack entry per navigate) |
| EC-5 | 2, 3 | `DefaultTitleRepositoryTest.details_missingFields_areNull`; manual: How to test 5 |
| EC-9 | 3 | manual: long title wraps, synopsis wraps (no max lines) |

## Out of scope
- Favorite, trailer -> 04-favorite-and-trailer
- From search results -> 05-search
- Cached, offline, and proper error states -> 06-offline-and-states

## Risks
- `type == 3` (theatrical) may be missing for streaming-only titles; falls back to the first regional date, then to `release_date`.
- TMDB returns an empty `episode_run_time` for many series, so runtime is often hidden for TV.

## Deviations
- `app` now depends on `:core:data`: feature navigation functions take a `Title`, so the NavHost needs the type on its classpath.
- `FlowRow` is stable in this Compose BOM; no `ExperimentalLayoutApi` opt-in.
- Below the artwork the page background is `SampleColors.HeaderFade`, matching the design's opaque fade layer, instead of the screen gradient.
- Without the trailer button (subtask 04), "Elenco principal" sits 30 dp below the synopsis.
