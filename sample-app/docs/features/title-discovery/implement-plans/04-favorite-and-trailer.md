# Plan: Favorite and trailer

Requirement: [04 - Favorite and trailer](../subtasks/04-favorite-and-trailer.md)
Status: Done

## Summary
Add a `favorites` Room table (database v2, auto-migration) behind a `FavoritesRepository`, pick a YouTube
trailer from TMDB's `videos` in the details request, and add the favorite button and "Assistir trailer"
to Title details. The primary button moves from onboarding into the design system so both screens share it.

## Decisions
- Room `favorites` table, `SampleDatabase` version 1 → 2 with `AutoMigration(from = 1, to = 2)`. (user; rejected: DataStore set)
- Primary key `(id, media_type)` (auto) — TMDB movie and TV ids overlap.
- `setFavorite(id, mediaType, favorite: Boolean)` writes the target state, not a toggle; insert ignores duplicates, delete is idempotent (auto) — the last tap wins with no duplicates (EC-8).
- Trailer: `append_to_response` gains `videos` and the request adds `include_video_language=<pt|en>,en,null`; the first YouTube `Trailer` wins, official ones first; URL `https://www.youtube.com/watch?v=<key>` (auto) — TMDB filters videos by the request language otherwise.
- The trailer opens with `Intent.ACTION_VIEW`; `ActivityNotFoundException` shows a snackbar (auto) — EC-10.
- `SamplePrimaryButton(label, onClick, modifier, icon: Painter? = null)` in `core:designsystem`, replacing onboarding's private button (auto) — the design reuses the same button; rejected: a second copy in details.
- `ic_favorite_filled` (needs-export) placeholder: Material's filled heart path, tinted pink (auto).

## Reuse
| Need | Existing component | Location |
|---|---|---|
| Database, DI | `SampleDatabase`, `DatabaseModule` | core/database |
| Details request | `TmdbApi.getDetails`, `NetworkTitleDetails` | core/network |
| Details model, repository | `TitleDetails`, `DefaultTitleRepository` | core/data |
| Details state | `DetailsViewModel`, `DetailsUiState` | feature/details |
| Button | onboarding `PrimaryButton` → `SamplePrimaryButton` | feature/onboarding → core/designsystem |

## Design mapping
| Design | Code |
|---|---|
| Favorite button 32 dp circle, surface, top right (30 dp from right) | `FavoriteButton` (private, details) |
| Favorited state _(proposed)_ | `ic_favorite_filled` tinted `colorScheme.secondary` |
| "Assistir trailer" 44 dp, `ic_play` 20 dp, 10 dp gap | `SamplePrimaryButton(icon = ic_play)`, 30 dp below synopsis; cast heading 46 dp below it |
| No trailer _(proposed)_ | button not composed; cast heading 30 dp below synopsis |
| Trailer can't open _(proposed)_ | `SnackbarHost` in `DetailsScreen` |

## Assets
| Asset | Status | Action | Destination | Step |
|---|---|---|---|---|
| ic_favorite | provided | VectorDrawable from `assets/ic_favorite.svg` | feature/details/src/main/res/drawable/ic_favorite.xml | 4 |
| ic_favorite_filled | needs-export | placeholder filled heart | feature/details/src/main/res/drawable/ic_favorite_filled.xml | 4 |
| ic_play | provided | VectorDrawable from `assets/ic_play.svg` | feature/details/src/main/res/drawable/ic_play.xml | 4 |

## Changes overview
| Module | Files (new / modified) | Why |
|---|---|---|
| core:database | `model/FavoriteEntity.kt`, `dao/FavoriteDao.kt` (new), `SampleDatabase.kt`, `di/DatabaseModule.kt`, `schemas/…/2.json` (generated) | favorites table |
| core:network | `model/NetworkTitleDetails.kt`, `retrofit/RetrofitTmdbNetwork.kt`, `retrofit/TmdbRequestInterceptor.kt` | videos |
| core:data | `repository/FavoritesRepository.kt`, `DefaultFavoritesRepository.kt` (new), `di/DataModule.kt`, `model/TitleDetails.kt`, `model/TitleMappers.kt`, tests | favorites, trailer |
| core:testing | `repository/TestFavoritesRepository.kt` (new) | test double |
| core:designsystem | `component/SamplePrimaryButton.kt` (new) | shared button |
| feature:onboarding | `OnboardingScreen.kt` | use shared button |
| feature:details | ViewModel, UiState, Screen, strings, drawables, tests | UI |

## Steps
### Step 1: Favorites table  (AC-8, EC-8) ✅
- Files: `core/database/.../model/FavoriteEntity.kt`, `dao/FavoriteDao.kt` (new); `SampleDatabase.kt`, `di/DatabaseModule.kt` (modify)
- What: `@Entity("favorites", primaryKeys = ["id", "media_type"]) FavoriteEntity(id: Long, @ColumnInfo("media_type") mediaType: String)`; `FavoriteDao`: `@Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id AND media_type = :mediaType)") fun observeIsFavorite(id, mediaType): Flow<Boolean>`, `@Insert(onConflict = IGNORE) suspend fun insert(entity)`, `@Delete suspend fun delete(entity)`; `version = 2`, `autoMigrations = [AutoMigration(from = 1, to = 2)]`; provide `FavoriteDao`.
- Unit tests: none (Room DAO tests are instrumented; covered through the repository fake).
- Compile: `./gradlew :core:database:compileDebugKotlin` (also generates `schemas/…/2.json`)

### Step 2: FavoritesRepository  (AC-8, EC-8) ✅
- Files: `core/data/.../repository/FavoritesRepository.kt`, `DefaultFavoritesRepository.kt`, `di/DataModule.kt`, `core/testing/.../TestFavoritesRepository.kt`, `core/data/src/test/.../testdoubles/FakeFavoriteDao.kt`, `DefaultFavoritesRepositoryTest.kt`
- What: `interface FavoritesRepository { fun observeIsFavorite(id: Long, mediaType: MediaType): Flow<Boolean>; suspend fun setFavorite(id: Long, mediaType: MediaType, favorite: Boolean) }`; default impl on the IO dispatcher.
- Unit tests: `DefaultFavoritesRepositoryTest.setFavorite_true_isObserved`, `setFavorite_false_removes`, `setFavorite_repeated_keepsLastState`, `sameIdDifferentMediaType_isSeparate`.
- Compile: `./gradlew :core:data:compileDebugKotlin :core:data:compileDebugUnitTestKotlin :core:testing:compileDebugKotlin`

### Step 3: Trailer in TitleDetails  (AC-9) ✅
- Files: `NetworkTitleDetails.kt` (`videos: NetworkVideos?`, `NetworkVideo(key, site, type, official)`), `RetrofitTmdbNetwork.kt` (append `videos`, `include_video_language`), `TmdbRequestInterceptor.kt` (`internal fun tmdbVideoLanguages(locale)` = `"pt,en,null"` or `"en,null"`), `TitleDetails.kt` (`trailerUrl: String?`), `TitleMappers.kt`, tests
- Unit tests: `DefaultTitleRepositoryTest.trailer_picksOfficialYouTubeTrailer`, `trailer_noYouTubeTrailer_isNull`; `TmdbRequestInterceptorTest.tmdbVideoLanguages_portuguese_includesEnglish`.
- Compile: `./gradlew :core:network:compileDebugUnitTestKotlin :core:data:compileDebugUnitTestKotlin :feature:details:compileDebugUnitTestKotlin`

### Step 4: Details UI  (AC-8, AC-9, EC-8, EC-10) ✅
- Files: `core/designsystem/.../component/SamplePrimaryButton.kt` (new), `feature/onboarding/.../OnboardingScreen.kt`, `feature/details/...` `DetailsUiState.kt`, `DetailsViewModel.kt`, `DetailsScreen.kt`, `res/values(-pt)/strings.xml`, `res/drawable/ic_favorite.xml`, `ic_favorite_filled.xml`, `ic_play.xml`, `DetailsViewModelTest.kt`
- What:
  - `DetailsUiState.Success(details, isFavorite: Boolean)`; the ViewModel combines the details load with `favoritesRepository.observeIsFavorite(id, mediaType)`; `fun setFavorite(favorite: Boolean)`.
  - `FavoriteButton(isFavorite, onClick)` over the artwork, shown only in `Success`; `SamplePrimaryButton` with `ic_play` when `trailerUrl != null`; open via `ACTION_VIEW`, snackbar on `ActivityNotFoundException`.
  - Strings (en / pt): `feature_details_trailer` "Watch trailer"/"Assistir trailer", `feature_details_trailer_error` "Couldn't open the trailer"/"Não foi possível abrir o trailer", `feature_details_favorite_add` "Add to favorites"/"Adicionar aos favoritos", `feature_details_favorite_remove` "Remove from favorites"/"Remover dos favoritos".
- Unit tests: `DetailsViewModelTest.success_includesFavoriteState`, `setFavorite_updatesState`, `setFavorite_repeated_lastWins`.
- Compile: `./gradlew :feature:details:compileDebugUnitTestKotlin :feature:onboarding:compileDebugKotlin :app:assembleDebug`

Tests after the last step: `./gradlew testDebugUnitTest`

## Traceability
| AC / EC | Step(s) | Test(s) |
|---|---|---|
| AC-8 | 1, 2, 4 | `DefaultFavoritesRepositoryTest.*`, `DetailsViewModelTest.setFavorite_updatesState`; manual: restart keeps state |
| AC-9 | 3, 4 | `DefaultTitleRepositoryTest.trailer_*`; manual: How to test 3–4 |
| EC-8 | 2, 4 | `DefaultFavoritesRepositoryTest.setFavorite_repeated_keepsLastState`, `DetailsViewModelTest.setFavorite_repeated_lastWins` |
| EC-10 | 4 | manual: device without a browser or YouTube (no unit test for `startActivity`) |

## Out of scope
- A favorites list (out of scope for the feature)

## Risks
- The 1 → 2 auto-migration is only verified by building; a Room migration test is instrumented and not run here.

## Deviations
- `SamplePrimaryButton` is 44 dp tall everywhere; onboarding's button grows from 43 dp to 44 dp.
- The favorite button sits 8 dp below the status bar to line up with "Voltar" rather than at the design's fixed 32 dp from the top.
