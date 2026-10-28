package com.example.sample.core.network.model

import com.google.gson.annotations.SerializedName

data class NetworkPage(
    @SerializedName("results") val results: List<NetworkTitle>,
)

/** A movie has [title], a TV show has [name]. */
data class NetworkTitle(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("popularity") val popularity: Double = 0.0,
    @SerializedName("vote_average") val voteAverage: Double = 0.0,
    /** `movie` or `tv`; only set in mixed lists such as recommendations. */
    @SerializedName("media_type") val mediaType: String? = null,
)
