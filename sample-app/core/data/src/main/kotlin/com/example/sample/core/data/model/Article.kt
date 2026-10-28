package com.example.sample.core.data.model

import java.time.Instant

/** The article model shared by every feature. Network and database models never leave `core`. */
data class Article(
    val id: Long,
    val title: String,
    val summary: String,
    val url: String,
    val newsSite: String,
    val publishedAt: Instant,
)
