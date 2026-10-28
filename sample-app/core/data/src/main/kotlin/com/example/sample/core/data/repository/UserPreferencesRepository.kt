package com.example.sample.core.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    /** Whether the user has tapped through onboarding on this device. */
    val onboardingCompleted: Flow<Boolean>

    suspend fun completeOnboarding()
}
