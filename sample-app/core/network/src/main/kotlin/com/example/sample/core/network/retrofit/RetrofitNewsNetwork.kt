package com.example.sample.core.network.retrofit

import com.example.sample.core.network.NewsNetworkDataSource
import com.example.sample.core.network.model.NetworkArticle
import com.example.sample.core.network.model.NetworkArticleResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

/** Spaceflight News API v4: https://api.spaceflightnewsapi.net/v4/docs/ */
interface NewsApi {
    @GET("v4/articles/")
    suspend fun getArticles(@Query("limit") limit: Int): NetworkArticleResponse
}

internal class RetrofitNewsNetwork @Inject constructor(
    private val api: NewsApi,
) : NewsNetworkDataSource {
    override suspend fun getArticles(limit: Int): List<NetworkArticle> = api.getArticles(limit).results
}
