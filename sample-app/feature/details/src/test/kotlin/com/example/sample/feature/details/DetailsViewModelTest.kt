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
import java.io.IOException

class DetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestTitleRepository()
    private val favorites = TestFavoritesRepository()
    private val savedStateHandle = SavedStateHandle(mapOf("id" to 7L, "mediaType" to "Tv"))

    private fun viewModel() = DetailsViewModel(savedStateHandle, repository, favorites)

    @Test
    fun cachedDetails_shownWhileRefreshing() = runTest {
        repository.sendTitleDetails(details)

        viewModel().uiState.test {
            assertEquals(DetailsUiState.Success(details, isFavorite = false), expectMostRecentItem())
            assertEquals(1, repository.detailsRefreshCount)
        }
    }

    @Test
    fun offline_withoutCache_showsOfflineError() = runTest {
        repository.refreshError = IOException("offline")

        viewModel().uiState.test {
            assertEquals(DetailsUiState.Error(offline = true), expectMostRecentItem())
        }
    }

    @Test
    fun refreshOffline_withCache_showsNotice() = runTest {
        repository.refreshError = IOException("offline")
        repository.sendTitleDetails(details)

        viewModel().uiState.test {
            assertEquals(DetailsUiState.Success(details, isFavorite = false, offlineNotice = true), expectMostRecentItem())
        }
    }

    @Test
    fun refreshFailed_withCache_flagsRefreshFailed() = runTest {
        repository.refreshError = IllegalStateException("server")
        repository.sendTitleDetails(details)

        viewModel().uiState.test {
            assertEquals(DetailsUiState.Success(details, isFavorite = false, refreshFailed = true), expectMostRecentItem())
        }
    }

    @Test
    fun retry_afterError_recovers() = runTest {
        repository.refreshError = IllegalStateException("server")
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(DetailsUiState.Error(offline = false), expectMostRecentItem())
            repository.refreshError = null
            repository.sendTitleDetails(details)
            viewModel.retry()

            assertEquals(DetailsUiState.Success(details, isFavorite = false), expectMostRecentItem())
        }
    }

    @Test
    fun success_includesFavoriteState() = runTest {
        repository.sendTitleDetails(details)
        favorites.setFavorite(7, MediaType.Tv, favorite = true)

        viewModel().uiState.test {
            assertEquals(DetailsUiState.Success(details, isFavorite = true), expectMostRecentItem())
        }
    }

    @Test
    fun setFavorite_updatesState() = runTest {
        repository.sendTitleDetails(details)
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.setFavorite(true)

            assertEquals(DetailsUiState.Success(details, isFavorite = true), expectMostRecentItem())
        }
    }

    @Test
    fun setFavorite_repeated_lastWins() = runTest {
        repository.sendTitleDetails(details)
        val viewModel = viewModel()

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
