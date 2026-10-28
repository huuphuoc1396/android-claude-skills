package com.example.sample.core.data.testdoubles

import com.example.sample.core.database.dao.ArticleDao
import com.example.sample.core.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeArticleDao : ArticleDao {
    private val articles = MutableStateFlow(emptyMap<Long, ArticleEntity>())

    override fun observeArticles(): Flow<List<ArticleEntity>> =
        articles.map { byId -> byId.values.sortedByDescending { it.publishedAt } }

    override suspend fun upsertArticles(articles: List<ArticleEntity>) {
        this.articles.update { current -> current + articles.associateBy { it.id } }
    }
}
