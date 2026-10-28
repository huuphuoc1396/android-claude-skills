package com.example.sample.feature.home

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

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestTitleRepository()

    @Test
    fun initialState_isFilmsLoading() {
        val viewModel = HomeViewModel(repository)

        assertEquals(HomeUiState(Category.Films, HomeContent.Loading), viewModel.uiState.value)
    }

    @Test
    fun load_success_showsFeaturedAndPopular() = runTest {
        repository.titles = mapOf(Category.Films to CategoryTitles(featured = listOf(film), popular = listOf(series)))
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(
                HomeUiState(Category.Films, HomeContent.Success(featured = listOf(film), popular = listOf(series))),
                expectMostRecentItem(),
            )
        }
    }

    @Test
    fun selectCategory_loadsThatCategory() = runTest {
        repository.titles = mapOf(Category.Anime to CategoryTitles(featured = listOf(film), popular = emptyList()))
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            viewModel.selectCategory(Category.Anime)

            assertEquals(
                HomeUiState(Category.Anime, HomeContent.Success(featured = listOf(film), popular = emptyList())),
                expectMostRecentItem(),
            )
            assertEquals(listOf(Category.Films, Category.Anime), repository.requestedCategories)
        }
    }

    @Test
    fun load_failure_showsError() = runTest {
        repository.shouldFail = true
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState(Category.Films, HomeContent.Error), expectMostRecentItem())
        }
    }

    @Test
    fun retry_afterError_recovers() = runTest {
        repository.shouldFail = true
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeContent.Error, expectMostRecentItem().content)
            repository.shouldFail = false
            viewModel.retry()

            assertEquals(HomeContent.Success(featured = emptyList(), popular = emptyList()), expectMostRecentItem().content)
            assertEquals(2, repository.requestedCategories.size)
        }
    }

    private val film = Title(id = 1, mediaType = MediaType.Movie, name = "Matrix", posterUrl = null)
    private val series = Title(id = 2, mediaType = MediaType.Tv, name = "Lucifer", posterUrl = null)
}
