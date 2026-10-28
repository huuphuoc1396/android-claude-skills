package com.example.sample.core.testing.repository

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.model.TitleDetails
import com.example.sample.core.data.repository.TitleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Test double for [TitleRepository]. Push cached data with [sendCategoryTitles] and [sendTitleDetails]
 * (like the real cache, a category starts empty and details start null); make refreshes throw with [refreshError].
 */
class TestTitleRepository : TitleRepository {
    private val categoryTitles = MutableStateFlow(emptyMap<Category, CategoryTitles>())
    private val titleDetails = MutableStateFlow<Map<Pair<Long, MediaType>, TitleDetails?>>(emptyMap())

    var searchResults: Map<String, List<Title>> = emptyMap()

    /** Thrown by [search] when set, e.g. an `IOException` for offline. */
    var searchError: Exception? = null

    /** Thrown by every refresh when set, e.g. an `IOException` for offline. */
    var refreshError: Exception? = null

    private val _refreshedCategories = mutableListOf<Category>()
    val refreshedCategories: List<Category> get() = _refreshedCategories

    var detailsRefreshCount = 0
        private set

    override fun observeCategoryTitles(category: Category): Flow<CategoryTitles> =
        categoryTitles.map { it[category] ?: CategoryTitles(featured = emptyList(), popular = emptyList()) }

    override suspend fun refreshCategory(category: Category) {
        _refreshedCategories += category
        refreshError?.let { throw it }
    }

    override suspend fun search(query: String): List<Title> {
        searchError?.let { throw it }
        return searchResults[query].orEmpty()
    }

    override fun observeTitleDetails(id: Long, mediaType: MediaType): Flow<TitleDetails?> =
        titleDetails.map { it[id to mediaType] }

    override suspend fun refreshTitleDetails(id: Long, mediaType: MediaType) {
        detailsRefreshCount++
        refreshError?.let { throw it }
    }

    fun sendCategoryTitles(category: Category, titles: CategoryTitles) {
        categoryTitles.value += category to titles
    }

    fun sendTitleDetails(details: TitleDetails) {
        titleDetails.value += (details.id to details.mediaType) to details
    }
}
