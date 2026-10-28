package com.example.sample.core.network

import com.example.sample.core.network.model.NetworkArticle

/** Remote source of articles. Repositories depend on this interface, never on Retrofit directly. */
interface NewsNetworkDataSource {
    suspend fun getArticles(limit: Int = 20): List<NetworkArticle>
}
