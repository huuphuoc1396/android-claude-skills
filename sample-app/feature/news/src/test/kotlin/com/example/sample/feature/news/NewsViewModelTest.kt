package com.example.sample.feature.news

import app.cash.turbine.test
import com.example.sample.core.data.model.Article
import com.example.sample.core.testing.MainDispatcherRule
import com.example.sample.core.testing.repository.TestArticleRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class NewsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestArticleRepository()

    @Test
    fun uiState_isSuccess_whenArticlesAreCached() = runTest {
        val viewModel = NewsViewModel(repository)

        viewModel.uiState.test {
            assertEquals(NewsUiState.Loading, awaitItem())
            repository.sendArticles(listOf(article))
            assertEquals(NewsUiState.Success(listOf(article)), awaitItem())
        }
    }

    @Test
    fun uiState_isEmpty_whenRefreshSucceedsWithNoArticles() = runTest {
        val viewModel = NewsViewModel(repository)

        viewModel.uiState.test {
            assertEquals(NewsUiState.Loading, awaitItem())
            repository.sendArticles(emptyList())
            assertEquals(NewsUiState.Empty, awaitItem())
        }
    }

    @Test
    fun uiState_isError_whenRefreshFailsWithNoCache() = runTest {
        repository.shouldFailRefresh = true
        val viewModel = NewsViewModel(repository)

        viewModel.uiState.test {
            assertEquals(NewsUiState.Loading, awaitItem())
            repository.sendArticles(emptyList())
            assertEquals(NewsUiState.Error, awaitItem())
        }
    }

    @Test
    fun refresh_afterError_retriesAndRecovers() = runTest {
        repository.shouldFailRefresh = true
        val viewModel = NewsViewModel(repository)
        repository.sendArticles(emptyList())

        viewModel.uiState.test {
            assertEquals(NewsUiState.Error, awaitItem())
            repository.shouldFailRefresh = false
            viewModel.refresh()
            assertEquals(NewsUiState.Empty, expectMostRecentItem())
            assertEquals(2, repository.refreshCount)
        }
    }

    private val article = Article(
        id = 1,
        title = "Title",
        summary = "Summary",
        url = "https://example.com/1",
        newsSite = "Example",
        publishedAt = Instant.parse("2026-01-01T00:00:00Z"),
    )
}
