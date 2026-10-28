package com.example.sample.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sample.feature.onboarding.OnboardingRoute
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingDestination

fun NavGraphBuilder.onboardingScreen(onContinue: () -> Unit) {
    composable<OnboardingDestination> {
        OnboardingRoute(onContinue = onContinue)
    }
}
