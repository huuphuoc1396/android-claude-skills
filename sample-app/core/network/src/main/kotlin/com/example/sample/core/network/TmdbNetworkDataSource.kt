package com.example.sample.core.network

import com.example.sample.core.network.model.NetworkTitle
import com.example.sample.core.network.model.NetworkTitleDetails

/** Remote source of TMDB titles. Repositories depend on this interface, never on Retrofit directly. */
interface TmdbNetworkDataSource {
    /** The country whose release dates are shown, matching the content language. */
    val region: String

    suspend fun discover(query: TmdbDiscoverQuery): List<NetworkTitle>

    /** Movies, TV shows, and people matching [query]; each result carries its `media_type`. */
    suspend fun search(query: String): List<NetworkTitle>

    /** A title with its credits and recommendations (and regional release dates for movies). */
    suspend fun getDetails(mediaType: TmdbMediaType, id: Long): NetworkTitleDetails
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
