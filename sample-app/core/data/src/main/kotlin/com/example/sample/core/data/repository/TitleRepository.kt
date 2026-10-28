package com.example.sample.core.data.repository

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.CategoryTitles
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.model.TitleDetails

interface TitleRepository {
    /** The featured and most popular titles of [category]. Throws on network errors. */
    suspend fun getCategoryTitles(category: Category): CategoryTitles

    /** Movies and TV shows matching [query]. Throws an [java.io.IOException] when offline. */
    suspend fun search(query: String): List<Title>

    /** Details, cast, and recommendations of one title. Throws on network errors. */
    suspend fun getTitleDetails(id: Long, mediaType: MediaType): TitleDetails
}
