package com.example.sample.feature.search

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.testing.MainDispatcherRule
import com.example.sample.core.testing.repository.TestTitleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class SearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestTitleRepository()

    private fun viewModel(query: String = "matrix") =
        SearchViewModel(SavedStateHandle(mapOf("query" to query)), repository)

    @Test
    fun initialQuery_showsResults() = runTest {
        repository.searchResults = mapOf("matrix" to listOf(matrix))

        viewModel().uiState.test {
            assertEquals(SearchUiState("matrix", SearchResults.Found(listOf(matrix))), expectMostRecentItem())
        }
    }

    @Test
    fun noMatches_showsNoResults() = runTest {
        viewModel(query = "zzzzqqqq").uiState.test {
            assertEquals(SearchResults.NoResults, expectMostRecentItem().results)
        }
    }

    @Test
    fun ioException_showsOffline() = runTest {
        repository.searchError = IOException("offline")

        viewModel().uiState.test {
            assertEquals(SearchResults.Offline, expectMostRecentItem().results)
        }
    }

    @Test
    fun otherException_showsError() = runTest {
        repository.searchError = IllegalStateException("server")

        viewModel().uiState.test {
            assertEquals(SearchResults.Error, expectMostRecentItem().results)
        }
    }

    @Test
    fun submit_newQuery_searchesAgain() = runTest {
        repository.searchResults = mapOf("lucifer" to listOf(lucifer))
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.onQueryChange("lucifer")
            viewModel.submit()

            assertEquals(SearchUiState("lucifer", SearchResults.Found(listOf(lucifer))), expectMostRecentItem())
        }
    }

    @Test
    fun retry_afterOffline_recovers() = runTest {
        repository.searchError = IOException("offline")
        repository.searchResults = mapOf("matrix" to listOf(matrix))
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SearchResults.Offline, expectMostRecentItem().results)
            repository.searchError = null
            viewModel.retry()

            assertEquals(SearchResults.Found(listOf(matrix)), expectMostRecentItem().results)
        }
    }

    private val matrix = Title(id = 1, mediaType = MediaType.Movie, name = "Matrix", posterUrl = null)
    private val lucifer = Title(id = 2, mediaType = MediaType.Tv, name = "Lucifer", posterUrl = null)
}
