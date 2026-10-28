package com.example.sample.feature.details

import com.example.sample.core.data.model.TitleDetails

sealed interface DetailsUiState {
    data object Loading : DetailsUiState

    /** Loading the title failed. */
    data object Error : DetailsUiState

    data class Success(
        val details: TitleDetails,
        val isFavorite: Boolean,
    ) : DetailsUiState
}
