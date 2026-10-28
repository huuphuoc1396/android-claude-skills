package com.example.sample.feature.search

import com.example.sample.core.data.model.Title

data class SearchUiState(
    val query: String,
    val results: SearchResults,
)

sealed interface SearchResults {
    data object Loading : SearchResults

    /** The search couldn't reach TMDB (EC-3). */
    data object Offline : SearchResults

    data object Error : SearchResults

    data object NoResults : SearchResults

    data class Found(val titles: List<Title>) : SearchResults
}
