package com.example.sample.core.network.model

import com.google.gson.annotations.SerializedName

data class NetworkArticleResponse(
    @SerializedName("results") val results: List<NetworkArticle>,
)

data class NetworkArticle(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("url") val url: String,
    @SerializedName("news_site") val newsSite: String,
    @SerializedName("published_at") val publishedAt: String,
)
