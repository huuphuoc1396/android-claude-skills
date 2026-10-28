# Plan: Onboarding on first launch

Requirement: [03 - Onboarding on first launch](../subtasks/03-onboarding.md)
Status: Done

## Summary
Store an "onboarding completed" flag in Preferences DataStore (new `:core:datastore`), expose it through a
`UserPreferencesRepository` in `:core:data`, and add a new `:feature:onboarding` module. The app reads the
flag once at start to pick the start destination; "Acessar" saves the flag and replaces Onboarding with
Home on the back stack.

## Decisions
- Preferences DataStore `1.2.1` in a new `:core:datastore` module. (user; rejected: SharedPreferences, Room table)
- New `:feature:onboarding` module. (user; rejected: inside :app)
- `MainActivityViewModel` exposes `Loading | Success(onboardingCompleted)`; the NavHost is shown on the first `Success` and its start destination is captured once with `remember`, so saving the flag later doesn't rebuild the graph (auto) — a changing `startDestination` resets the back stack.
- While `Loading` the activity shows only the dark background (auto) — the DataStore read takes milliseconds; rejected: adding the SplashScreen library.
- "Acessar" saves the flag first, then navigates to Home with `popUpTo<OnboardingDestination> { inclusive = true }` (auto) — back from Home exits the app (AC-1).
- Headline weights via `AnnotatedString.fromHtml` with `<b>` on the nouns and "muito mais."; the base is Medium, so "Tudo sobre" is Medium instead of Light (auto) — `fromHtml` has no font-weight 300; rejected: hand-built spans per language.
- Placeholder art: the exported PNG as `drawable-nodpi/img_onboarding_bg.png` and the wordmark SVG as a VectorDrawable, until the designer exports final assets (auto) — subtask scope.
- The overlay's background blur is not reproduced (auto) — `Modifier.blur` blurs content, not what's behind it, and needs API 31.

## Reuse
| Need | Existing component | Location |
|---|---|---|
| Repository + Hilt binding | `TitleRepository`, `DataModule` | core/data |
| IO dispatcher | `@Dispatcher(SampleDispatchers.IO)` | core/common |
| Accent gradient, colors, type | `SampleGradients.accent`, `SampleTheme` | core/designsystem |
| ViewModel tests | `MainDispatcherRule`, Turbine | core/testing |
| Navigation shape | `HomeNavigation.kt` | feature/home |

## Design mapping
| Design | Code |
|---|---|
| onboard screen | `OnboardingRoute` / `OnboardingScreen`, `OnboardingDestination` |
| Background image + bottom gradient #15151D 85% (37%) → transparent (69%) | `Image` + `Brush.verticalGradient` from bottom; color `SampleColors.SurfaceDeep`-like `#15151D` = `colorScheme.background` at 0.85 alpha |
| Logo 176 × 15 | `img_logo_tmdb` VectorDrawable |
| Headline Inter 32, body Inter 400 15 | `typography.headlineLarge` with 32 sp; `bodyMedium` with Normal weight, 24 sp line height |
| Primary button 315 × 43, radius 60, accent gradient, label 600 16 | `PrimaryButton` (private) |
| Nav: onboard → home, removes onboard | `navigate(HomeDestination) { popUpTo<OnboardingDestination> { inclusive = true } }` in the NavHost |

## Assets
| Asset | Status | Action | Destination | Step |
|---|---|---|---|---|
| img_onboarding_bg | needs-export | exported PNG as placeholder | feature/onboarding/src/main/res/drawable-nodpi/img_onboarding_bg.png | 3 |
| img_logo_tmdb | needs-export | exported wordmark SVG → VectorDrawable placeholder | feature/onboarding/src/main/res/drawable/img_logo_tmdb.xml | 3 |

## Changes overview
| Module | Files (new / modified) | Why |
|---|---|---|
| gradle | libs.versions.toml | DataStore |
| core:datastore | new module: `UserPreferencesDataSource`, `di/DataStoreModule`, test | flag storage |
| core:data | `repository/UserPreferencesRepository.kt`, `DefaultUserPreferencesRepository.kt`, `di/DataModule.kt`, build.gradle.kts | repository |
| core:testing | `repository/TestUserPreferencesRepository.kt` | test double |
| feature:onboarding | new module | screen |
| app | `MainActivity.kt`, `MainActivityViewModel.kt` (new), `SampleNavHost.kt`, build.gradle.kts, test | start destination |
| settings.gradle.kts | modify | modules |

## Steps
### Step 1: DataStore flag  (AC-1) ✅
- Files: `gradle/libs.versions.toml`, `settings.gradle.kts`, `core/datastore/build.gradle.kts`, `UserPreferencesDataSource.kt`, `di/DataStoreModule.kt` (new)
- What:
  - Catalog `datastore = "1.2.1"`, `androidx-datastore-preferences`.
  - `class UserPreferencesDataSource @Inject constructor(private val dataStore: DataStore<Preferences>)`: `val onboardingCompleted: Flow<Boolean>` (default false), `suspend fun setOnboardingCompleted()`.
  - `DataStoreModule` provides a singleton `DataStore<Preferences>` via `PreferenceDataStoreFactory.create(scope = CoroutineScope(ioDispatcher + SupervisorJob())) { context.preferencesDataStoreFile("user_preferences") }`.
- Unit tests: `UserPreferencesDataSourceTest` (TemporaryFolder-backed DataStore): `onboardingCompleted_isFalseByDefault`, `setOnboardingCompleted_isPersisted`.
- Compile: `./gradlew :core:datastore:compileDebugKotlin :core:datastore:compileDebugUnitTestKotlin`

### Step 2: UserPreferencesRepository  (AC-1) ✅
- Files: `core/data/build.gradle.kts`, `repository/UserPreferencesRepository.kt`, `repository/DefaultUserPreferencesRepository.kt`, `di/DataModule.kt`, `core/testing/.../TestUserPreferencesRepository.kt`
- What: `interface UserPreferencesRepository { val onboardingCompleted: Flow<Boolean>; suspend fun completeOnboarding() }`; default impl delegates to the data source; `TestUserPreferencesRepository` backed by a `MutableStateFlow(false)`.
- Unit tests: none beyond step 1 (pure delegation).
- Compile: `./gradlew :core:data:compileDebugKotlin :core:testing:compileDebugKotlin`

### Step 3: Onboarding feature  (AC-1) ✅
- Files: `settings.gradle.kts`, `feature/onboarding/build.gradle.kts`, `OnboardingViewModel.kt`, `OnboardingScreen.kt`, `navigation/OnboardingNavigation.kt`, `res/values(-pt)/strings.xml`, `res/drawable/img_logo_tmdb.xml`, `res/drawable-nodpi/img_onboarding_bg.png` (new)
- What:
  - `OnboardingViewModel(userPreferencesRepository)`: `fun completeOnboarding(onDone: () -> Unit)` launches, saves, then calls `onDone`.
  - `OnboardingDestination`; `NavGraphBuilder.onboardingScreen(onContinue: () -> Unit)`.
  - Screen per onboard.md: full-bleed image (`ContentScale.Crop`), bottom gradient, bottom-anchored column (30 dp margins): logo, 19 dp, headline, 19 dp, body, 19 dp, button, 31 dp + navigation bar inset.
  - Strings (en / pt), headline in CDATA with `<b>`: `feature_onboarding_headline` "All about <b>movies</b>, <b>series</b>, <b>anime</b> and <b>much more.</b>" / "Tudo sobre <b>filmes</b>, <b>séries</b>, <b>animes</b> e <b>muito mais.</b>"; `feature_onboarding_body` "Stay up to date on movies, series, anime and much more." / "Fique por dentro das informações de filmes, séries, animes e muito mais."; `feature_onboarding_continue` "Get started" / "Acessar"; `feature_onboarding_logo` "The Movie DB" (logo description, both).
- Unit tests: `OnboardingViewModelTest.completeOnboarding_savesFlagAndCallsOnDone`.
- Compile: `./gradlew :feature:onboarding:compileDebugKotlin :feature:onboarding:compileDebugUnitTestKotlin`

### Step 4: Start destination  (AC-1) ✅
- Files: `app/build.gradle.kts`, `MainActivityViewModel.kt` (new), `MainActivity.kt`, `navigation/SampleNavHost.kt`, `app/src/test/.../MainActivityViewModelTest.kt` (new)
- What:
  - `sealed interface MainActivityUiState { Loading; data class Success(val onboardingCompleted: Boolean) }`; `MainActivityViewModel` maps the repository flow with `stateIn(Eagerly, Loading)`.
  - `MainActivity`: `SampleTheme { Box(background) { if (state is Success) SampleNavHost(onboardingCompleted = remember { state.onboardingCompleted }) } }`.
  - `SampleNavHost(onboardingCompleted: Boolean)`: start = Home if completed else Onboarding; `onboardingScreen(onContinue = { navController.navigate(HomeDestination) { popUpTo<OnboardingDestination> { inclusive = true } } })`.
  - `app` test deps: `:core:testing`, coroutines-test, turbine.
- Unit tests: `MainActivityViewModelTest.uiState_isLoadingThenReflectsFlag`.
- Compile: `./gradlew :app:assembleDebug :app:compileDebugUnitTestKotlin`

Tests after the last step: `./gradlew testDebugUnitTest`

## Traceability
| AC / EC | Step(s) | Test(s) |
|---|---|---|
| AC-1 | 1–4 | `UserPreferencesDataSourceTest.*`, `OnboardingViewModelTest.*`, `MainActivityViewModelTest.*`; manual: How to test 1–3 |

## Out of scope
- Final artwork and official TMDB logo (designer)

## Risks
- The placeholder background is a 1.1 MB PNG of a copyrighted still; it must be replaced before release (design.md needs-export).

## Deviations
- `app` gained `lifecycle-runtime-compose` and `lifecycle-viewmodel-compose` (for `collectAsStateWithLifecycle` and `viewModels`) and unit-test dependencies for `MainActivityViewModelTest`.
- `core:datastore` depends on `core:common` for the IO dispatcher qualifier.
- `MainActivityViewModelTest` uses a `StandardTestDispatcher` main rule so the `Loading` state is observable.
