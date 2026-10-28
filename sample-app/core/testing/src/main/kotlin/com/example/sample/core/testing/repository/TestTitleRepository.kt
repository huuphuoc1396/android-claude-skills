package com.example.sample.core.testing.repository

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.TitleDetails
import com.example.sample.core.data.repository.TitleRepository
import java.io.IOException

/** Test double for [TitleRepository]. Set [titles] per category and [details] per id; make calls fail with [shouldFail]. */
class TestTitleRepository : TitleRepository {
    var titles: Map<Category, CategoryTitles> = emptyMap()
    var details: Map<Long, TitleDetails> = emptyMap()
    var shouldFail = false

    private val _requestedCategories = mutableListOf<Category>()
    val requestedCategories: List<Category> get() = _requestedCategories

    override suspend fun getCategoryTitles(category: Category): CategoryTitles {
        _requestedCategories += category
        if (shouldFail) throw IOException("Network error")
        return titles[category] ?: CategoryTitles(featured = emptyList(), popular = emptyList())
    }

    override suspend fun getTitleDetails(id: Long, mediaType: MediaType): TitleDetails {
        if (shouldFail) throw IOException("Network error")
        return details.getValue(id)
    }
}
