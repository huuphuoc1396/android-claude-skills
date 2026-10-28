package com.example.sample.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

/** One poster in one Home section; [position] keeps TMDB's order. */
@Entity(tableName = "category_titles", primaryKeys = ["category", "section", "position"])
data class CategoryTitleEntity(
    val category: String,
    val section: String,
    val position: Int,
    val id: Long,
    @ColumnInfo(name = "media_type") val mediaType: String,
    val name: String,
    @ColumnInfo(name = "poster_url") val posterUrl: String?,
)
