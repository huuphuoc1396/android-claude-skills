package com.example.sample.core.testing.repository

import com.example.sample.core.data.model.Article
import com.example.sample.core.data.repository.ArticleRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.IOException

/** Test double for [ArticleRepository]. Push data with [sendArticles]; make [refresh] fail with [shouldFailRefresh]. */
class TestArticleRepository : ArticleRepository {
    private val articlesFlow = MutableSharedFlow<List<Article>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    var shouldFailRefresh = false
    var refreshCount = 0
        private set

    override fun observeArticles(): Flow<List<Article>> = articlesFlow

    override suspend fun refresh() {
        refreshCount++
        if (shouldFailRefresh) throw IOException("Refresh failed")
    }

    fun sendArticles(articles: List<Article>) {
        articlesFlow.tryEmit(articles)
    }
}
