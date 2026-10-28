package com.example.sample.feature.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.TitleDetails
import com.example.sample.core.testing.MainDispatcherRule
import com.example.sample.core.testing.repository.TestFavoritesRepository
import com.example.sample.core.testing.repository.TestTitleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestTitleRepository()
    private val favorites = TestFavoritesRepository()
    private val savedStateHandle = SavedStateHandle(mapOf("id" to 7L, "mediaType" to "Tv"))

    @Test
    fun load_readsRouteAndShowsDetails() = runTest {
        repository.details = mapOf(7L to details)
        val viewModel = DetailsViewModel(savedStateHandle, repository, favorites)

        viewModel.uiState.test {
            assertEquals(DetailsUiState.Success(details, isFavorite = false), expectMostRecentItem())
        }
    }

    @Test
    fun load_failure_showsError() = runTest {
        repository.shouldFail = true
        val viewModel = DetailsViewModel(savedStateHandle, repository, favorites)

        viewModel.uiState.test {
            assertEquals(DetailsUiState.Error, expectMostRecentItem())
        }
    }

    @Test
    fun retry_afterError_recovers() = runTest {
        repository.shouldFail = true
        repository.details = mapOf(7L to details)
        val viewModel = DetailsViewModel(savedStateHandle, repository, favorites)

        viewModel.uiState.test {
            assertEquals(DetailsUiState.Error, expectMostRecentItem())
            repository.shouldFail = false
            viewModel.retry()

            assertEquals(DetailsUiState.Success(details, isFavorite = false), expectMostRecentItem())
        }
    }

    @Test
    fun success_includesFavoriteState() = runTest {
        repository.details = mapOf(7L to details)
        favorites.setFavorite(7, MediaType.Tv, favorite = true)
        val viewModel = DetailsViewModel(savedStateHandle, repository, favorites)

        viewModel.uiState.test {
            assertEquals(DetailsUiState.Success(details, isFavorite = true), expectMostRecentItem())
        }
    }

    @Test
    fun setFavorite_updatesState() = runTest {
        repository.details = mapOf(7L to details)
        val viewModel = DetailsViewModel(savedStateHandle, repository, favorites)

        viewModel.uiState.test {
            viewModel.setFavorite(true)

            assertEquals(DetailsUiState.Success(details, isFavorite = true), expectMostRecentItem())
        }
    }

    @Test
    fun setFavorite_repeated_lastWins() = runTest {
        repository.details = mapOf(7L to details)
        val viewModel = DetailsViewModel(savedStateHandle, repository, favorites)

        viewModel.uiState.test {
            listOf(true, false, true, false).forEach(viewModel::setFavorite)

            assertEquals(DetailsUiState.Success(details, isFavorite = false), expectMostRecentItem())
        }
    }

    private val details = TitleDetails(
        id = 7,
        mediaType = MediaType.Tv,
        name = "Lucifer",
        releaseDate = null,
        releaseCountry = null,
        ratingPercent = null,
        runtimeMinutes = null,
        synopsis = null,
        artworkUrl = null,
        cast = emptyList(),
        genres = emptyList(),
        recommendations = emptyList(),
        trailerUrl = null,
    )
}
