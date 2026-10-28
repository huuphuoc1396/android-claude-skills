package com.example.sample.core.data.model

import java.time.LocalDate

/** Everything Title details shows. A null or empty field means TMDB has no value and the UI hides it. */
data class TitleDetails(
    val id: Long,
    val mediaType: MediaType,
    val name: String,
    val releaseDate: LocalDate?,
    val releaseCountry: String?,
    val ratingPercent: Int?,
    val runtimeMinutes: Int?,
    val synopsis: String?,
    val artworkUrl: String?,
    val cast: List<CastMember>,
    val genres: List<String>,
    val recommendations: List<Title>,
)

data class CastMember(
    val name: String,
    val photoUrl: String?,
)
