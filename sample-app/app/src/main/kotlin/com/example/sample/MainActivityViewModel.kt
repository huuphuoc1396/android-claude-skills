package com.example.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userPreferencesRepository.onboardingCompleted
        .map(MainActivityUiState::Success)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainActivityUiState.Loading)
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(val onboardingCompleted: Boolean) : MainActivityUiState
}
