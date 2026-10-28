package com.example.sample.feature.news

import com.example.sample.core.data.model.Article

sealed interface NewsUiState {
    data object Loading : NewsUiState

    data object Empty : NewsUiState

    /** The first load failed and there is nothing cached to show. */
    data object Error : NewsUiState

    data class Success(val articles: List<Article>) : NewsUiState
}
