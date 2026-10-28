package com.example.sample.core.data.repository

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles

interface TitleRepository {
    /** The featured and most popular titles of [category]. Throws on network errors. */
    suspend fun getCategoryTitles(category: Category): CategoryTitles
}
