package com.example.sample.core.data.repository

import com.example.sample.core.data.testdoubles.FakeArticleDao
import com.example.sample.core.data.testdoubles.FakeNewsNetworkDataSource
import com.example.sample.core.network.model.NetworkArticle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class OfflineFirstArticleRepositoryTest {
    private val testScope = TestScope(StandardTestDispatcher())
    private lateinit var network: FakeNewsNetworkDataSource
    private lateinit var repository: OfflineFirstArticleRepository

    @Before
    fun setUp() {
        network = FakeNewsNetworkDataSource()
        repository = OfflineFirstArticleRepository(
            articleDao = FakeArticleDao(),
            network = network,
            ioDispatcher = StandardTestDispatcher(testScope.testScheduler),
        )
    }

    @Test
    fun refresh_storesNetworkArticles_newestFirst() = testScope.runTest {
        network.articles = listOf(
            networkArticle(id = 1, publishedAt = "2026-01-01T00:00:00Z"),
            networkArticle(id = 2, publishedAt = "2026-02-01T00:00:00Z"),
        )

        repository.refresh()

        assertEquals(listOf(2L, 1L), repository.observeArticles().first().map { it.id })
    }

    @Test
    fun refresh_whenNetworkFails_throwsAndKeepsCache() = testScope.runTest {
        network.articles = listOf(networkArticle(id = 1))
        repository.refresh()
        network.shouldFail = true

        val error = runCatching { repository.refresh() }.exceptionOrNull()

        assertTrue(error is IOException)
        assertEquals(listOf(1L), repository.observeArticles().first().map { it.id })
    }

    private fun networkArticle(id: Long, publishedAt: String = "2026-01-01T00:00:00Z") = NetworkArticle(
        id = id,
        title = "Title $id",
        summary = "Summary $id",
        url = "https://example.com/$id",
        newsSite = "Example",
        publishedAt = publishedAt,
    )
}
