package com.example.sample.core.data.model

import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.model.NetworkTitle

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"

internal fun NetworkTitle.asExternalModel(mediaType: TmdbMediaType) = Title(
    id = id,
    mediaType = when (mediaType) {
        TmdbMediaType.Movie -> MediaType.Movie
        TmdbMediaType.Tv -> MediaType.Tv
    },
    name = title ?: name.orEmpty(),
    posterUrl = posterPath?.let { POSTER_BASE_URL + it },
)
