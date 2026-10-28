package com.example.sample.core.network

import com.example.sample.core.network.model.NetworkTitle

/** Remote source of TMDB titles. Repositories depend on this interface, never on Retrofit directly. */
interface TmdbNetworkDataSource {
    suspend fun discover(query: TmdbDiscoverQuery): List<NetworkTitle>
}

enum class TmdbMediaType(val path: String) {
    Movie("movie"),
    Tv("tv"),
}

/** One `discover/{movie|tv}` request. [originCountries] uses TMDB's `|` for OR, e.g. `BR|MX`. */
data class TmdbDiscoverQuery(
    val mediaType: TmdbMediaType,
    val sortBy: String,
    val genreId: Int? = null,
    val originCountries: String? = null,
    val minVoteCount: Int? = null,
)
