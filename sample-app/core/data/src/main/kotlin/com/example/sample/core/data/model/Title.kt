package com.example.sample.core.data.model

/** A movie or TV show as every feature sees it. Network and database models never leave `core`. */
data class Title(
    val id: Long,
    val mediaType: MediaType,
    val name: String,
    val posterUrl: String?,
)

enum class MediaType {
    Movie,
    Tv,
}

data class CategoryTitles(
    val featured: List<Title>,
    val popular: List<Title>,
)
