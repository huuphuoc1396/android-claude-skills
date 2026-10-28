package com.example.sample.core.testing.repository

import com.example.sample.core.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Test double for [UserPreferencesRepository], starting with onboarding not completed. */
class TestUserPreferencesRepository : UserPreferencesRepository {
    private val completed = MutableStateFlow(false)

    override val onboardingCompleted: Flow<Boolean> = completed

    override suspend fun completeOnboarding() {
        completed.value = true
    }
}
