package com.example.sample.feature.details

import com.example.sample.core.data.model.TitleDetails

sealed interface DetailsUiState {
    /** Nothing cached yet and the first refresh is running. */
    data object Loading : DetailsUiState

    /** Nothing cached and the refresh failed; [offline] when there was no connection (EC-2). */
    data class Error(val offline: Boolean) : DetailsUiState

    /**
     * Cached details. [offlineNotice] when the latest refresh couldn't connect (EC-1);
     * [refreshFailed] when it failed for another reason (EC-4).
     */
    data class Success(
        val details: TitleDetails,
        val isFavorite: Boolean,
        val offlineNotice: Boolean = false,
        val refreshFailed: Boolean = false,
    ) : DetailsUiState
}
