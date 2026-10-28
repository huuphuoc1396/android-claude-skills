package com.example.sample.core.testing.repository

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.repository.TitleRepository
import java.io.IOException

/** Test double for [TitleRepository]. Set [titles] per category; make calls fail with [shouldFail]. */
class TestTitleRepository : TitleRepository {
    var titles: Map<Category, CategoryTitles> = emptyMap()
    var shouldFail = false

    private val _requestedCategories = mutableListOf<Category>()
    val requestedCategories: List<Category> get() = _requestedCategories

    override suspend fun getCategoryTitles(category: Category): CategoryTitles {
        _requestedCategories += category
        if (shouldFail) throw IOException("Network error")
        return titles[category] ?: CategoryTitles(featured = emptyList(), popular = emptyList())
    }
}
