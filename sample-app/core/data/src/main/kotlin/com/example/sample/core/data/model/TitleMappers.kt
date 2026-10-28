package com.example.sample.core.data.model

import com.example.sample.core.database.model.CachedCastMember
import com.example.sample.core.database.model.CachedTitle
import com.example.sample.core.database.model.CategoryTitleEntity
import com.example.sample.core.database.model.TitleDetailsEntity
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
private const val YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v="
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
        trailerUrl = videos?.results.orEmpty()
            .filter { it.site == "YouTube" && it.type == "Trailer" }
            .sortedByDescending { it.official }
            .firstOrNull()
            ?.let { YOUTUBE_WATCH_URL + it.key },
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

internal fun String.toTmdbMediaType(): TmdbMediaType? = TmdbMediaType.entries.firstOrNull { it.path == this }

private fun TmdbMediaType.asExternalModel() = when (this) {
    TmdbMediaType.Movie -> MediaType.Movie
    TmdbMediaType.Tv -> MediaType.Tv
}

internal fun Title.asCategoryEntity(category: String, section: String, position: Int) = CategoryTitleEntity(
    category = category,
    section = section,
    position = position,
    id = id,
    mediaType = mediaType.name,
    name = name,
    posterUrl = posterUrl,
)

internal fun CategoryTitleEntity.asExternalModel() = Title(
    id = id,
    mediaType = MediaType.valueOf(mediaType),
    name = name,
    posterUrl = posterUrl,
)

internal fun TitleDetails.asEntity() = TitleDetailsEntity(
    id = id,
    mediaType = mediaType.name,
    name = name,
    releaseDate = releaseDate?.toString(),
    releaseCountry = releaseCountry,
    ratingPercent = ratingPercent,
    runtimeMinutes = runtimeMinutes,
    synopsis = synopsis,
    artworkUrl = artworkUrl,
    trailerUrl = trailerUrl,
    cast = cast.map { CachedCastMember(name = it.name, photoUrl = it.photoUrl) },
    genres = genres,
    recommendations = recommendations.map {
        CachedTitle(id = it.id, mediaType = it.mediaType.name, name = it.name, posterUrl = it.posterUrl)
    },
)

internal fun TitleDetailsEntity.asExternalModel() = TitleDetails(
    id = id,
    mediaType = MediaType.valueOf(mediaType),
    name = name,
    releaseDate = releaseDate?.let(LocalDate::parse),
    releaseCountry = releaseCountry,
    ratingPercent = ratingPercent,
    runtimeMinutes = runtimeMinutes,
    synopsis = synopsis,
    artworkUrl = artworkUrl,
    cast = cast.map { CastMember(name = it.name, photoUrl = it.photoUrl) },
    genres = genres,
    recommendations = recommendations.map {
        Title(id = it.id, mediaType = MediaType.valueOf(it.mediaType), name = it.name, posterUrl = it.posterUrl)
    },
    trailerUrl = trailerUrl,
)

internal fun MediaType.asTmdbMediaType() = when (this) {
    MediaType.Movie -> TmdbMediaType.Movie
    MediaType.Tv -> TmdbMediaType.Tv
}
