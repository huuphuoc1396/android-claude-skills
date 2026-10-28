package com.example.sample.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

/** A favorited title. TMDB movie and TV ids overlap, so the media type is part of the key. */
@Entity(tableName = "favorites", primaryKeys = ["id", "media_type"])
data class FavoriteEntity(
    val id: Long,
    @ColumnInfo(name = "media_type") val mediaType: String,
)
