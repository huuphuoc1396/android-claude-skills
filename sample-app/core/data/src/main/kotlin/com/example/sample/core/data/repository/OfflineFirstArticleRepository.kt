package com.example.sample.core.data.repository

import com.example.sample.core.common.Dispatcher
import com.example.sample.core.common.SampleDispatchers
import com.example.sample.core.data.model.Article
import com.example.sample.core.data.model.asEntity
import com.example.sample.core.data.model.asExternalModel
import com.example.sample.core.database.dao.ArticleDao
import com.example.sample.core.network.NewsNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** The database is the source of truth; the network only refreshes it. */
internal class OfflineFirstArticleRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val network: NewsNetworkDataSource,
    @Dispatcher(SampleDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ArticleRepository {

    override fun observeArticles(): Flow<List<Article>> =
        articleDao.observeArticles().map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun refresh() = withContext(ioDispatcher) {
        val articles = network.getArticles()
        articleDao.upsertArticles(articles.map { it.asEntity() })
    }
}
