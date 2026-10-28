package com.example.sample.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Small user settings kept on the device. Repositories depend on this, never on DataStore directly. */
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[OnboardingCompleted] ?: false }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences -> preferences[OnboardingCompleted] = true }
    }

    private companion object {
        val OnboardingCompleted = booleanPreferencesKey("onboarding_completed")
    }
}
