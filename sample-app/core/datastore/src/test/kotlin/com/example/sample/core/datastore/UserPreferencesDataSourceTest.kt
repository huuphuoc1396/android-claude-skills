package com.example.sample.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UserPreferencesDataSourceTest {
    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private fun dataSource() = UserPreferencesDataSource(
        PreferenceDataStoreFactory.create(scope = testScope.backgroundScope) {
            tmpFolder.newFile("user_preferences.preferences_pb")
        },
    )

    @Test
    fun onboardingCompleted_isFalseByDefault() = testScope.runTest {
        assertFalse(dataSource().onboardingCompleted.first())
    }

    @Test
    fun setOnboardingCompleted_isPersisted() = testScope.runTest {
        val dataSource = dataSource()

        dataSource.setOnboardingCompleted()

        assertTrue(dataSource.onboardingCompleted.first())
    }
}
