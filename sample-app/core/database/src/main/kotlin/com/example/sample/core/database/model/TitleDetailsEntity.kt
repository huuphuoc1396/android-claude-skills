package com.example.sample.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.serialization.Serializable

/** A Title details screen as last loaded. Lists are stored as JSON (see `Converters`). */
@Entity(tableName = "title_details", primaryKeys = ["id", "media_type"])
data class TitleDetailsEntity(
    val id: Long,
    @ColumnInfo(name = "media_type") val mediaType: String,
    val name: String,
    /** ISO `yyyy-MM-dd`. */
    @ColumnInfo(name = "release_date") val releaseDate: String?,
    @ColumnInfo(name = "release_country") val releaseCountry: String?,
    @ColumnInfo(name = "rating_percent") val ratingPercent: Int?,
    @ColumnInfo(name = "runtime_minutes") val runtimeMinutes: Int?,
    val synopsis: String?,
    @ColumnInfo(name = "artwork_url") val artworkUrl: String?,
    @ColumnInfo(name = "trailer_url") val trailerUrl: String?,
    val cast: List<CachedCastMember>,
    val genres: List<String>,
    val recommendations: List<CachedTitle>,
)

@Serializable
data class CachedCastMember(
    val name: String,
    val photoUrl: String?,
)

@Serializable
data class CachedTitle(
    val id: Long,
    val mediaType: String,
    val name: String,
    val posterUrl: String?,
)
