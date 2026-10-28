package com.example.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.sample.feature.details.navigation.detailsScreen
import com.example.sample.feature.details.navigation.navigateToDetails
import com.example.sample.feature.home.navigation.HomeDestination
import com.example.sample.feature.home.navigation.homeScreen
import com.example.sample.feature.onboarding.navigation.OnboardingDestination
import com.example.sample.feature.onboarding.navigation.onboardingScreen

/** Every feature registers its screens here through its `NavGraphBuilder.<feature>Screen()` extension. */
@Composable
fun SampleNavHost(
    onboardingCompleted: Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (onboardingCompleted) HomeDestination else OnboardingDestination,
        modifier = modifier,
    ) {
        onboardingScreen(
            onContinue = {
                // Back from Home exits the app instead of returning to onboarding (AC-1).
                navController.navigate(HomeDestination) {
                    popUpTo<OnboardingDestination> { inclusive = true }
                }
            },
        )
        homeScreen(onTitleClick = navController::navigateToDetails)
        detailsScreen(
            onBack = navController::popBackStack,
            onTitleClick = navController::navigateToDetails,
        )
    }
}
