package com.example.sample.feature.home

import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.Title

data class HomeUiState(
    val selectedCategory: Category,
    val content: HomeContent,
)

sealed interface HomeContent {
    data object Loading : HomeContent

    /** Loading the selected category failed. */
    data object Error : HomeContent

    data class Success(
        val featured: List<Title>,
        val popular: List<Title>,
    ) : HomeContent
}
