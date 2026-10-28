package com.example.sample.core.data.repository

import com.example.sample.core.data.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    /** Articles from the local cache, newest first. Emits again after every [refresh]. */
    fun observeArticles(): Flow<List<Article>>

    /** Fetches the latest articles and stores them in the cache. Throws on network errors. */
    suspend fun refresh()
}
