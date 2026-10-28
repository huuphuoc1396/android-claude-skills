package com.example.sample.core.data.model

import com.example.sample.core.network.TmdbMediaType
import com.example.sample.core.network.model.NetworkTitle
import com.example.sample.core.network.model.NetworkTitleDetails
import java.time.LocalDate
import kotlin.math.roundToInt

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val POSTER_BASE_URL = "${IMAGE_BASE_URL}w342"
private const val ARTWORK_BASE_URL = "${IMAGE_BASE_URL}w780"
private const val PROFILE_BASE_URL = "${IMAGE_BASE_URL}w185"
private const val THEATRICAL_RELEASE = 3
private const val MAX_CAST = 10
private const val MAX_RECOMMENDATIONS = 20

internal fun NetworkTitle.asExternalModel(mediaType: TmdbMediaType) = Title(
    id = id,
    mediaType = mediaType.asExternalModel(),
    name = title ?: name.orEmpty(),
    posterUrl = posterPath?.let { POSTER_BASE_URL + it },
)

internal fun NetworkTitleDetails.asExternalModel(mediaType: TmdbMediaType, region: String): TitleDetails {
    val (date, country) = when (mediaType) {
        TmdbMediaType.Movie -> regionalRelease(region) ?: (releaseDate.toLocalDate() to null)
        TmdbMediaType.Tv -> firstAirDate.toLocalDate() to originCountry?.firstOrNull()
    }
    return TitleDetails(
        id = id,
        mediaType = mediaType.asExternalModel(),
        name = title ?: name.orEmpty(),
        releaseDate = date,
        releaseCountry = country.takeIf { date != null },
        ratingPercent = if (voteCount > 0) (voteAverage * 10).roundToInt() else null,
        runtimeMinutes = (runtime ?: episodeRunTime?.firstOrNull())?.takeIf { it > 0 },
        synopsis = overview?.takeIf { it.isNotBlank() },
        artworkUrl = posterPath?.let { ARTWORK_BASE_URL + it },
        cast = credits?.cast.orEmpty()
            .sortedBy { it.order }
            .take(MAX_CAST)
            .map { CastMember(name = it.name, photoUrl = it.profilePath?.let { path -> PROFILE_BASE_URL + path }) },
        genres = genres.orEmpty().map { it.name },
        recommendations = recommendations?.results.orEmpty()
            .take(MAX_RECOMMENDATIONS)
            .map { it.asExternalModel(it.mediaType?.toTmdbMediaType() ?: mediaType) },
    )
}

/** The theatrical release in [region], else its first release there; null if the region has none. */
private fun NetworkTitleDetails.regionalRelease(region: String): Pair<LocalDate?, String?>? {
    val dates = releaseDates?.results?.firstOrNull { it.country == region }?.releaseDates.orEmpty()
    val release = dates.firstOrNull { it.type == THEATRICAL_RELEASE } ?: dates.firstOrNull() ?: return null
    return release.releaseDate.toLocalDate()?.let { it to region }
}

/** TMDB dates are `yyyy-MM-dd` or a full timestamp; blank means unknown. */
private fun String?.toLocalDate(): LocalDate? =
    this?.takeIf { it.length >= 10 }?.let { LocalDate.parse(it.take(10)) }

private fun String.toTmdbMediaType(): TmdbMediaType? = TmdbMediaType.entries.firstOrNull { it.path == this }

private fun TmdbMediaType.asExternalModel() = when (this) {
    TmdbMediaType.Movie -> MediaType.Movie
    TmdbMediaType.Tv -> MediaType.Tv
}

internal fun MediaType.asTmdbMediaType() = when (this) {
    MediaType.Movie -> TmdbMediaType.Movie
    MediaType.Tv -> TmdbMediaType.Tv
}
