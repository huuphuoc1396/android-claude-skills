package com.example.sample.core.data.repository

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.model.TitleDetails
import kotlinx.coroutines.flow.Flow

interface TitleRepository {
    /** The cached featured and most popular titles of [category]; both lists are empty until a refresh succeeds. */
    fun observeCategoryTitles(category: Category): Flow<CategoryTitles>

    /** Fetches [category] and replaces its cache. Throws on network errors and keeps the old cache. */
    suspend fun refreshCategory(category: Category)

    /** Movies and TV shows matching [query]. Throws an [java.io.IOException] when offline. */
    suspend fun search(query: String): List<Title>

    /** The cached details of one title, or null if it was never loaded. */
    fun observeTitleDetails(id: Long, mediaType: MediaType): Flow<TitleDetails?>

    /** Fetches details, cast, and recommendations and caches them. Throws on network errors. */
    suspend fun refreshTitleDetails(id: Long, mediaType: MediaType)
}
