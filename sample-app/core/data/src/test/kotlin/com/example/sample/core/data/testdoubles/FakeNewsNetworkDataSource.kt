package com.example.sample.core.data.testdoubles

import com.example.sample.core.network.NewsNetworkDataSource
import com.example.sample.core.network.model.NetworkArticle
import java.io.IOException

class FakeNewsNetworkDataSource : NewsNetworkDataSource {
    var articles: List<NetworkArticle> = emptyList()
    var shouldFail = false

    override suspend fun getArticles(limit: Int): List<NetworkArticle> {
        if (shouldFail) throw IOException("Network error")
        return articles.take(limit)
    }
}
