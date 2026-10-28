package com.example.sample.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.core.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    /** Saves that onboarding is done, then calls [onDone] so the next launch opens Home (AC-1). */
    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.completeOnboarding()
            onDone()
        }
    }
}
