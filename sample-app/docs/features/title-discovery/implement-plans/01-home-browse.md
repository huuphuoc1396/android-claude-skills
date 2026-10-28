# Plan: Browse Home by category

Requirement: [01 - Browse Home by category](../subtasks/01-home-browse.md)
Status: Done

## Summary
Add a TMDB client next to the existing network code, a network-only `TitleRepository` that turns the four
categories into TMDB discover queries, and a new `:feature:home` module that renders the Home design in a
dark-only theme. News is removed once Home is the start destination. Caching is left to subtask 06, so the
repository follows the News data source pattern but not its database part yet.

## Decisions
- Coil 3 for poster images (`coil-compose`, `coil-network-okhttp`) — Compose-first, used by Now in Android. (user; rejected: Glide Compose)
- Inter through Compose downloadable Google Fonts (`ui-text-google-fonts`, from the Compose BOM) — no font files in the repo; system font until downloaded. (user; rejected: bundled .ttf, system font)
- New `:feature:home`; `:feature:news` and the news network, data, and testing code are deleted. The `articles` table and `ArticleDao` stay in `core:database` until subtask 06 changes the schema. (user; rejected: renaming feature:news)
- `SampleTheme` becomes dark-only and loses its `darkTheme` parameter. (user; rejected: keeping a light scheme)
- TMDB v3 API key from `local.properties` `TMDB_API_KEY`, exposed as `BuildConfig.TMDB_API_KEY` in `core:network` and sent as the `api_key` query parameter by an interceptor (auto) — local.properties is already gitignored; rejected: v4 bearer token (same secrecy, one more header type).
- The interceptor also adds `language`: `pt-BR` when `Locale.getDefault().language == "pt"`, else `en-US` (auto) — implements AC-10 for TMDB content at one point; rejected: passing language through every call.
- Gson stays the JSON converter (auto) — already used by `core:network`; rejected: adding kotlinx-serialization converter.
- "Mais populares" = discover `sort_by=popularity.desc`; featured = discover `sort_by=vote_average.desc` with a per-category minimum vote count (Films 1000, Series 500, Anime 200, Novelas 20) (auto) — TMDB has no "featured" list, and top-rated is the only ranking that works with genre and country filters; rejected: trending endpoints (cannot filter by genre/country).
- Anime merges one movie and one TV discover call and sorts the merged list by the same key (auto) — AC-3 says movies and TV.
- Novelas origin countries: `BR|MX|CO|AR|CL|PE|VE` (auto) — the largest Latin American producers on TMDB; `|` is TMDB's OR.
- Each section shows at most 20 titles (one TMDB page; Anime takes the top 20 after merging) (auto) — enough for a carousel.
- An empty section is hidden in this subtask; the empty state comes in 06 (auto).
- The search field is drawn but disabled until 05 (auto) — subtask scope.
- App name "Title Discovery" (en) / "Descobrir títulos" (pt) (auto) — avoids using the TMDB brand as the app name.
- UI strings: English in `values/`, Portuguese in `values-pt/`; per-app language via `generateLocaleConfig` (auto) — AC-10 maps any Portuguese to pt-BR.
- `ic_profile` (needs-export) gets a simple placeholder vector in `:feature:home` (auto).

## Reuse
| Need | Existing component | Location |
|---|---|---|
| IO dispatcher | `@Dispatcher(SampleDispatchers.IO)` | core/common/.../Dispatcher.kt |
| Retrofit + OkHttp setup | `NetworkModule` | core/network/.../di/NetworkModule.kt |
| Data source interface + Retrofit impl shape | `NewsNetworkDataSource`, `RetrofitNewsNetwork` | core/network/... |
| Repository + Hilt binding shape | `ArticleRepository`, `DataModule` | core/data/... |
| ViewModel + UiState shape, refresh error handling | `NewsViewModel`, `NewsUiState` | feature/news/... |
| Navigation registration | `NewsNavigation.kt`, `SampleNavHost` | feature/news/.../navigation, app/.../navigation |
| Loading and error UI | `SampleLoadingState`, `SampleErrorState` | core/designsystem/.../SampleStates.kt |
| ViewModel tests | `MainDispatcherRule`, Turbine, `TestArticleRepository` shape | core/testing/... |
| Feature module wiring | `sample.android.feature` convention plugin | build-logic/.../AndroidFeatureConventionPlugin.kt |

## Design mapping
| Design | Code |
|---|---|
| home screen | `HomeRoute` / `HomeScreen` in `:feature:home`, `HomeDestination` (start destination) |
| bg gradient, header purple gradient, accent purple gradient | `SampleGradients.background`, `.header`, `.accent` (new, theme) |
| accent pink | `colorScheme.secondary` |
| accent purple #8000FF | `colorScheme.primary` |
| search fill #36076B | `SampleColors.SearchFill` |
| surface #303243 | `colorScheme.surface` / `surfaceVariant` |
| surface deep #15161D, bg end #15151D | `colorScheme.background` = #15151D, `SampleColors.SurfaceDeep` |
| text primary / secondary / tertiary | `onBackground` #FFFFFF, `onSurfaceVariant` #CCCCCC, `SampleColors.TextTertiary` #BBBBBB |
| Inter weights 300–700 | `SampleTypography` (all styles use the Inter family) |
| Heading (Inter 700 26) | `typography.headlineSmall` override |
| Section heading (Inter 700 18) | `typography.titleMedium` override |
| Tab label (Inter 400/600 16) | `typography.bodyLarge` with weight override when selected |
| Search placeholder (Inter 500 15) | `typography.bodyMedium` override |
| Poster 124 × 188, radius 8 | `SamplePoster` (new, designsystem) — used by home now and search/details later |
| Category tabs (pill) | `CategoryTabs` (private, home) |
| Search field | `HomeSearchField` (private, home; disabled) |
| Profile avatar 40 pt, white stroke | `ProfileAvatar` (private, home) |
| Navigation rows | none in this subtask (poster taps come in 02) |

## Assets
| Asset | Status | Action | Destination | Step |
|---|---|---|---|---|
| ic_search | provided | convert `assets/ic_search.svg` to VectorDrawable | feature/home/src/main/res/drawable/ic_search.xml | 4 |
| ic_profile | needs-export | placeholder vector (circle + head silhouette) | feature/home/src/main/res/drawable/ic_profile.xml | 4 |

## Changes overview
| Module | Files (new / modified) | Why |
|---|---|---|
| gradle | libs.versions.toml (modify) | Coil, google-fonts entries |
| core:designsystem | build.gradle.kts, theme/Color.kt, theme/Type.kt, theme/Gradients.kt (new), theme/SampleTheme.kt, component/SamplePoster.kt (new), res/values/font_certs.xml (new) | dark theme, Inter, poster |
| core:network | build.gradle.kts, TmdbNetworkDataSource.kt, retrofit/RetrofitTmdbNetwork.kt, retrofit/TmdbRequestInterceptor.kt, model/NetworkTitle.kt (new), di/NetworkModule.kt; news files deleted in step 5 | TMDB client |
| core:data | model/Title.kt, model/Category.kt, model/TitleMappers.kt, repository/TitleRepository.kt, repository/DefaultTitleRepository.kt (new), di/DataModule.kt; news files deleted in step 5 | category rules |
| core:testing | repository/TestTitleRepository.kt (new); TestArticleRepository deleted | ViewModel test double |
| feature:home | new module | Home screen |
| feature:news | deleted | replaced by Home |
| app | build.gradle.kts, SampleNavHost.kt, res/values(-pt)/strings.xml, res/resources.properties (new) | start destination, app name, locales |
| settings.gradle.kts | modify | include home, drop news |

## Steps
### Step 1: Dark theme and Inter  (design) ✅
- Files: `gradle/libs.versions.toml`, `core/designsystem/build.gradle.kts`, `theme/Color.kt` (new), `theme/Type.kt` (new), `theme/Gradients.kt` (new), `theme/SampleTheme.kt` (modify), `res/values/font_certs.xml` (new)
- What:
  - Catalog: `androidx-compose-ui-text-google-fonts = { group = "androidx.compose.ui", name = "ui-text-google-fonts" }`; designsystem `implementation` it.
  - `Color.kt`: `internal val` design colors and `object SampleColors { SearchFill, SurfaceDeep, TextTertiary }`.
  - `Gradients.kt`: `object SampleGradients { val background: Brush; val header: Brush; val accent: Brush }` (vertical for background/header, horizontal for accent) with the stops from design.md.
  - `Type.kt`: `GoogleFont.Provider("com.google.android.gms.fonts", "com.google.android.gms", R.array.com_google_android_gms_fonts_certs)`, `InterFamily` with weights 300, 400, 500, 600, 700; `SampleTypography` = default `Typography()` with every style's `fontFamily = InterFamily` and the overrides in Design mapping.
  - `SampleTheme(content)`: `MaterialTheme(colorScheme = DarkColors, typography = SampleTypography)`; remove `LightColors` and `darkTheme`.
  - `font_certs.xml`: the standard Google Play Services font certificate arrays (copy from the Android "Downloadable fonts" documentation).
- Unit tests: none (pure resources and values).
- Compile: `./gradlew :core:designsystem:compileDebugKotlin :app:compileDebugKotlin :feature:news:compileDebugKotlin`

### Step 2: TMDB network client  (AC-3, AC-10) ✅
- Files: `core/network/build.gradle.kts`, `TmdbNetworkDataSource.kt` (new), `model/NetworkTitle.kt` (new), `retrofit/RetrofitTmdbNetwork.kt` (new), `retrofit/TmdbRequestInterceptor.kt` (new), `di/NetworkModule.kt` (modify)
- What:
  - build.gradle.kts: `buildFeatures { buildConfig = true }`; `buildConfigField("String", "TMDB_API_KEY", "\"$key\"")` where `key` is read from `rootProject.file("local.properties")` via `java.util.Properties`, empty string if missing.
  - `enum class TmdbMediaType(val path: String) { Movie("movie"), Tv("tv") }` and `data class TmdbDiscoverQuery(mediaType, genreId: Int?, originCountries: String?, sortBy: String, minVoteCount: Int?)`.
  - `interface TmdbNetworkDataSource { suspend fun discover(query: TmdbDiscoverQuery): List<NetworkTitle> }`.
  - `NetworkTitle(id: Long, title: String?, name: String?, posterPath: String?, popularity: Double, voteAverage: Double)` + `NetworkPage(results: List<NetworkTitle>)`, Gson `@SerializedName`.
  - `TmdbApi`: `@GET("discover/{media}") suspend fun discover(@Path("media") media: String, @Query("with_genres") genres: Int?, @Query("with_origin_country") origin: String?, @Query("sort_by") sortBy: String, @Query("vote_count.gte") minVotes: Int?): NetworkPage`.
  - `TmdbRequestInterceptor`: adds `api_key=BuildConfig.TMDB_API_KEY` and `language=tmdbLanguage(Locale.getDefault())`; `internal fun tmdbLanguage(locale: Locale): String`.
  - `NetworkModule`: provide `TmdbApi` from a second Retrofit with base URL `https://api.themoviedb.org/3/` and an OkHttp client built from the shared one via `newBuilder().addInterceptor(TmdbRequestInterceptor())`; bind `RetrofitTmdbNetwork`. News API untouched until step 5.
- Unit tests: `TmdbRequestInterceptorTest` (core/network/src/test): `tmdbLanguage_portuguese_isPtBr`, `tmdbLanguage_portugalPortuguese_isPtBr`, `tmdbLanguage_english_isEnUs`, `tmdbLanguage_otherLanguage_isEnUs`.
- Compile: `./gradlew :core:network:compileDebugKotlin`

### Step 3: Categories and TitleRepository  (AC-2, AC-3, AC-4) ✅
- Files: `core/data/.../model/Category.kt`, `model/Title.kt`, `model/TitleMappers.kt`, `repository/TitleRepository.kt`, `repository/DefaultTitleRepository.kt` (new); `di/DataModule.kt` (modify); `core/testing/.../repository/TestTitleRepository.kt` (new); `core/data/src/test/.../testdoubles/FakeTmdbNetworkDataSource.kt` (new)
- What:
  - `enum class Category { Films, Series, Anime, Novelas }`.
  - `enum class MediaType { Movie, Tv }`; `data class Title(id: Long, mediaType: MediaType, name: String, posterUrl: String?)`.
  - `data class CategoryTitles(featured: List<Title>, popular: List<Title>)`.
  - `interface TitleRepository { suspend fun getCategoryTitles(category: Category): CategoryTitles }` — throws on network errors.
  - Mapper: `NetworkTitle.asExternalModel(mediaType)`: name = `title ?: name ?: ""`, posterUrl = `"https://image.tmdb.org/t/p/w342$posterPath"` or null.
  - `DefaultTitleRepository` (IO dispatcher): rules table in the class:
    - Films: Movie; Series: Tv; Anime: Movie + Tv, genre 16, origin `JP`; Novelas: Tv, genre 10766, origin `BR|MX|CO|AR|CL|PE|VE`.
    - Popular: `sortBy = "popularity.desc"`. Featured: `sortBy = "vote_average.desc"`, `minVoteCount` per Decisions.
    - Both sections fetched concurrently (`coroutineScope { async }`); for Anime, the movie and TV results are merged, sorted by the section's key (popularity or vote average) descending, and cut to 20.
  - `DataModule`: bind `DefaultTitleRepository` to `TitleRepository`.
  - `TestTitleRepository`: `var titles: Map<Category, CategoryTitles>`, `var shouldFail`, `val requestedCategories: List<Category>`.
- Unit tests: `DefaultTitleRepositoryTest`: `films_queriesMoviesWithoutFilters`, `series_queriesTvWithoutFilters`, `anime_queriesMovieAndTvAnimationFromJapan`, `anime_mergesAndSortsByPopularity`, `novelas_queriesSoapTvFromLatinAmerica`, `featured_sortsByVoteAverageWithMinVotes`, `mapsTitleOrName_andBuildsPosterUrl`, `networkError_isThrown`.
- Compile: `./gradlew :core:data:compileDebugKotlin :core:testing:compileDebugKotlin`

### Step 4: Home feature  (AC-2, AC-4, AC-10, AC-12) ✅
- Files: `settings.gradle.kts` (include `:feature:home`), `gradle/libs.versions.toml` (Coil), `core/designsystem/build.gradle.kts` + `component/SamplePoster.kt` (new), `feature/home/build.gradle.kts`, `HomeUiState.kt`, `HomeViewModel.kt`, `HomeScreen.kt`, `navigation/HomeNavigation.kt`, `res/values/strings.xml`, `res/values-pt/strings.xml`, `res/drawable/ic_search.xml`, `res/drawable/ic_profile.xml` (new); `app/build.gradle.kts`, `app/.../navigation/SampleNavHost.kt` (modify)
- What:
  - Catalog: `coil = "3.3.0"`, `coil-compose`, `coil-network-okhttp` (`io.coil-kt.coil3`); designsystem `implementation` both.
  - `SamplePoster(imageUrl: String?, contentDescription: String?, modifier, onClick: (() -> Unit)? = null)`: 124 × 188, `RoundedCornerShape(8.dp)`, `AsyncImage` with `ContentScale.Crop`, surface-colored background as placeholder.
  - `HomeUiState(selectedCategory: Category, content: HomeContent)`; `sealed interface HomeContent { Loading; Error; data class Success(featured: List<Title>, popular: List<Title>) }`.
  - `HomeViewModel(titleRepository)`: `selectedCategory = MutableStateFlow(Category.Films)`, `retryTrigger`; `uiState` = `combine(selectedCategory, retryTrigger).flatMapLatest { flow { emit(Loading); emit(runCatching load → Success / Error) } }` (rethrow `CancellationException`), `stateIn(WhileSubscribed(5_000), HomeUiState(Films, Loading))`; `fun selectCategory(category)`, `fun retry()`.
  - `HomeScreen(uiState, onCategorySelected, onRetry)`: background brush; `LazyColumn` (vertical scroll) with header-gradient top area: heading + `ProfileAvatar` (no click), disabled `HomeSearchField`, `CategoryTabs` (horizontally scrollable `Row`, selected pill = secondary color, radius 70, padding 14×6), then per content: Loading → `SampleLoadingState`, Error → `SampleErrorState` with `feature_home_retry`, Success → featured `LazyRow` and, if not empty, "Mais populares" heading + `LazyRow`; empty rows hidden. 30 dp side margins; spacing from home.md. Status bar insets respected.
  - Strings (en / pt): `feature_home_heading` "What do you want to watch today?" / "O que você quer assistir hoje?"; `feature_home_search` "Search" / "Buscar"; `feature_home_tab_films` "Movies" / "Filmes"; `feature_home_tab_series` "Series" / "Séries"; `feature_home_tab_anime` "Anime" / "Animes"; `feature_home_tab_novelas` "Telenovelas" / "Novelas"; `feature_home_popular` "Most popular" / "Mais populares"; `feature_home_error` "Couldn't load" / "Não foi possível carregar"; `feature_home_retry` "Try again" / "Tentar novamente"; `feature_home_profile` "Profile" / "Perfil".
  - `HomeDestination` + `NavGraphBuilder.homeScreen()`; `SampleNavHost` start destination = `HomeDestination`, News still registered.
  - `app` depends on `:feature:home`.
- Unit tests: `HomeViewModelTest`: `initialState_isFilmsLoading`, `load_success_showsFeaturedAndPopular`, `selectCategory_loadsThatCategory`, `load_failure_showsError`, `retry_afterError_recovers`.
- Compile: `./gradlew :feature:home:compileDebugKotlin :app:compileDebugKotlin`

### Step 5: Remove News ✅
- Files (delete): `feature/news/`; `core/network/.../NewsNetworkDataSource.kt`, `model/NetworkArticle.kt`, `retrofit/RetrofitNewsNetwork.kt`; `core/data/.../model/Article.kt`, `ArticleMappers.kt`, `repository/ArticleRepository.kt`, `OfflineFirstArticleRepository.kt`, tests `OfflineFirstArticleRepositoryTest.kt`, `FakeArticleDao.kt`, `FakeNewsNetworkDataSource.kt`; `core/testing/.../TestArticleRepository.kt`. Modify: `settings.gradle.kts`, `app/build.gradle.kts`, `SampleNavHost.kt`, `NetworkModule.kt` (drop News Retrofit and binding; TMDB is the only API), `DataModule.kt`.
- What: `core:database` (ArticleEntity, ArticleDao, DatabaseModule) is not touched.
- Unit tests: none (deletions).
- Compile: `./gradlew :core:network:compileDebugKotlin :core:data:compileDebugKotlin :core:testing:compileDebugKotlin :app:compileDebugKotlin`

### Step 6: App name and per-app language  (AC-10) ✅
- Files: `app/build.gradle.kts` (`androidResources { generateLocaleConfig = true }`), `app/src/main/res/resources.properties` (new: `unqualifiedResLocale=en-US`), `app/src/main/res/values/strings.xml` (modify), `app/src/main/res/values-pt/strings.xml` (new).
- What: `app_name` "Title Discovery" / "Descobrir títulos".
- Unit tests: none.
- Compile: `./gradlew :app:assembleDebug`

Tests after the last step: `./gradlew testDebugUnitTest`

## Traceability
| AC / EC | Step(s) | Test(s) |
|---|---|---|
| AC-2 | 3, 4 | `HomeViewModelTest.selectCategory_loadsThatCategory`; manual: How to test 3 |
| AC-3 | 2, 3 | `DefaultTitleRepositoryTest.films_…`, `series_…`, `anime_…`, `novelas_…` |
| AC-4 | 3, 4 | `HomeViewModelTest.load_success_showsFeaturedAndPopular`; manual: How to test 2 |
| AC-10 | 2, 4, 6 | `TmdbRequestInterceptorTest.*`; manual: How to test 5 |
| AC-12 | 4 | manual: How to test 4 (avatar has no click handler) |

## Out of scope
- Poster taps and Title details -> 02-title-details
- Onboarding -> 03-onboarding
- Active search -> 05-search
- Caching, empty state, offline notice -> 06-offline-and-states
- Saving the tab across process death, screen-reader polish, TMDB attribution -> 07-resilience-and-accessibility

## Risks
- No `TMDB_API_KEY` in `local.properties` yet — every request returns 401 and Home shows the error state until the user adds one.
- `font_certs.xml` must match Google Play Services' certificates exactly; wrong values silently fall back to the system font. Forge copies them from the official documentation and the user checks the font visually.
- Coil `3.3.0` is the newest version known at planning time; if it doesn't resolve, forge uses the newest 3.x that does and notes it.
- Featured vote-count thresholds are guesses; Novelas or Anime featured rows may be short or empty (then hidden).
- `ic_profile` is a placeholder until the designer exports it (design.md needs-export).
- Removing News leaves the unused `articles` table in the database until 06.

## Deviations
- `font_certs.xml` copied from android/compose-samples (Jetchat, Apache 2.0, header kept), the source the Android documentation points to.
- `Title`, `MediaType`, and `CategoryTitles` share `model/Title.kt`; `Category` has its own file.
- `ic_search` keeps the SVG's 50% white fill, which matches the dimmed search placeholder.
- Coil `3.3.0` resolved as planned.
