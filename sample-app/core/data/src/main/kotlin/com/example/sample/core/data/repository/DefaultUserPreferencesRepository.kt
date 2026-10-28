package com.example.sample.core.data.repository

import com.example.sample.core.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DefaultUserPreferencesRepository @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {
    override val onboardingCompleted: Flow<Boolean> = dataSource.onboardingCompleted

    override suspend fun completeOnboarding() = dataSource.setOnboardingCompleted()
}
