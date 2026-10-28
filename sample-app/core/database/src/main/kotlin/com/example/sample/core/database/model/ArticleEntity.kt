package com.example.sample.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val summary: String,
    val url: String,
    @ColumnInfo(name = "news_site") val newsSite: String,
    @ColumnInfo(name = "published_at") val publishedAt: String,
)
