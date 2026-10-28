package com.example.sample

import app.cash.turbine.test
import com.example.sample.core.testing.MainDispatcherRule
import com.example.sample.core.testing.repository.TestUserPreferencesRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainActivityViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val repository = TestUserPreferencesRepository()

    @Test
    fun uiState_isLoadingThenReflectsFlag() = runTest {
        val viewModel = MainActivityViewModel(repository)

        viewModel.uiState.test {
            assertEquals(MainActivityUiState.Loading, awaitItem())
            assertEquals(MainActivityUiState.Success(onboardingCompleted = false), awaitItem())
            repository.completeOnboarding()
            assertEquals(MainActivityUiState.Success(onboardingCompleted = true), awaitItem())
        }
    }
}
