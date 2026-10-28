package com.example.sample.feature.home

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.testing.MainDispatcherRule
import com.example.sample.core.testing.repository.TestTitleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestTitleRepository()

    private fun viewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        HomeViewModel(savedStateHandle, repository)

    @Test
    fun initialState_isFilmsLoading() {
        assertEquals(HomeUiState(Category.Films, HomeContent.Loading), viewModel().uiState.value)
    }

    @Test
    fun cachedContent_shownWhileRefreshing() = runTest {
        repository.sendCategoryTitles(Category.Films, CategoryTitles(featured = listOf(film), popular = listOf(series)))
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(
                HomeUiState(Category.Films, HomeContent.Success(featured = listOf(film), popular = listOf(series))),
                expectMostRecentItem(),
            )
            assertEquals(listOf(Category.Films), repository.refreshedCategories)
        }
    }

    @Test
    fun refreshOffline_withCache_showsNotice() = runTest {
        repository.refreshError = IOException("offline")
        repository.sendCategoryTitles(Category.Films, CategoryTitles(featured = listOf(film), popular = emptyList()))

        viewModel().uiState.test {
            assertEquals(
                HomeContent.Success(featured = listOf(film), popular = emptyList(), offlineNotice = true),
                expectMostRecentItem().content,
            )
        }
    }

    @Test
    fun refreshFailed_withCache_flagsRefreshFailed() = runTest {
        repository.refreshError = IllegalStateException("server")
        repository.sendCategoryTitles(Category.Films, CategoryTitles(featured = listOf(film), popular = emptyList()))

        viewModel().uiState.test {
            assertEquals(
                HomeContent.Success(featured = listOf(film), popular = emptyList(), refreshFailed = true),
                expectMostRecentItem().content,
            )
        }
    }

    @Test
    fun emptyAfterRefresh_showsEmpty() = runTest {
        viewModel().uiState.test {
            assertEquals(HomeContent.Empty, expectMostRecentItem().content)
        }
    }

    @Test
    fun offline_withoutCache_showsOfflineError() = runTest {
        repository.refreshError = IOException("offline")

        viewModel().uiState.test {
            assertEquals(HomeContent.Error(offline = true), expectMostRecentItem().content)
        }
    }

    @Test
    fun retry_afterError_recovers() = runTest {
        repository.refreshError = IllegalStateException("server")
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(HomeContent.Error(offline = false), expectMostRecentItem().content)
            repository.refreshError = null
            repository.sendCategoryTitles(Category.Films, CategoryTitles(featured = listOf(film), popular = emptyList()))
            viewModel.retry()

            assertEquals(HomeContent.Success(featured = listOf(film), popular = emptyList()), expectMostRecentItem().content)
            assertEquals(2, repository.refreshedCategories.size)
        }
    }

    @Test
    fun selectCategory_refreshesThatCategory() = runTest {
        repository.sendCategoryTitles(Category.Anime, CategoryTitles(featured = listOf(film), popular = emptyList()))
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.selectCategory(Category.Anime)

            assertEquals(
                HomeUiState(Category.Anime, HomeContent.Success(featured = listOf(film), popular = emptyList())),
                expectMostRecentItem(),
            )
            assertEquals(listOf(Category.Films, Category.Anime), repository.refreshedCategories)
        }
    }

    @Test
    fun onQueryChange_updatesQuery() {
        val viewModel = viewModel()

        viewModel.onQueryChange("matrix")

        assertEquals("matrix", viewModel.query.value)
    }

    @Test
    fun query_restoredFromSavedState() {
        val viewModel = viewModel(SavedStateHandle(mapOf("query" to "lucifer")))

        assertEquals("lucifer", viewModel.query.value)
    }

    private val film = Title(id = 1, mediaType = MediaType.Movie, name = "Matrix", posterUrl = null)
    private val series = Title(id = 2, mediaType = MediaType.Tv, name = "Lucifer", posterUrl = null)
}
