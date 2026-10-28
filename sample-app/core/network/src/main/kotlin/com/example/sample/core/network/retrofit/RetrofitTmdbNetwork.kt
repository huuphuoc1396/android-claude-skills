package com.example.sample.core.network.retrofit

import com.example.sample.core.network.TmdbDiscoverQuery
import com.example.sample.core.network.TmdbNetworkDataSource
import com.example.sample.core.network.model.NetworkPage
import com.example.sample.core.network.model.NetworkTitle
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

/** TMDB API v3: https://developer.themoviedb.org/reference/intro/getting-started */
interface TmdbApi {
    @GET("discover/{media}")
    suspend fun discover(
        @Path("media") media: String,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") genreId: Int?,
        @Query("with_origin_country") originCountries: String?,
        @Query("vote_count.gte") minVoteCount: Int?,
    ): NetworkPage
}

internal class RetrofitTmdbNetwork @Inject constructor(
    private val api: TmdbApi,
) : TmdbNetworkDataSource {
    override suspend fun discover(query: TmdbDiscoverQuery): List<NetworkTitle> = api.discover(
        media = query.mediaType.path,
        sortBy = query.sortBy,
        genreId = query.genreId,
        originCountries = query.originCountries,
        minVoteCount = query.minVoteCount,
    ).results
}
