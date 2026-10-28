package com.example.sample.feature.onboarding

import com.example.sample.core.testing.MainDispatcherRule
import com.example.sample.core.testing.repository.TestUserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = TestUserPreferencesRepository()

    @Test
    fun completeOnboarding_savesFlagAndCallsOnDone() = runTest {
        val viewModel = OnboardingViewModel(repository)
        var doneCalled = false

        viewModel.completeOnboarding(onDone = { doneCalled = true })

        assertTrue(repository.onboardingCompleted.first())
        assertTrue(doneCalled)
    }
}
