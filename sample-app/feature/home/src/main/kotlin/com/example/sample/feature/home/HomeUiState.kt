package com.example.sample.feature.home

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.Title

data class HomeUiState(
    val selectedCategory: Category,
    val content: HomeContent,
)

sealed interface HomeContent {
    /** Nothing cached yet and the first refresh is running. */
    data object Loading : HomeContent

    /** A refresh succeeded but the category has no titles (EC-6). */
    data object Empty : HomeContent

    /** Nothing cached and the refresh failed; [offline] when there was no connection. */
    data class Error(val offline: Boolean) : HomeContent

    /**
     * Cached titles. [offlineNotice] when the latest refresh couldn't connect (EC-1);
     * [refreshFailed] when it failed for another reason (EC-4).
     */
    data class Success(
        val featured: List<Title>,
        val popular: List<Title>,
        val offlineNotice: Boolean = false,
        val refreshFailed: Boolean = false,
    ) : HomeContent
}
