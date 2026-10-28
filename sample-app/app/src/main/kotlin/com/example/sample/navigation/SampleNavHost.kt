package com.example.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.sample.feature.details.navigation.detailsScreen
import com.example.sample.feature.details.navigation.navigateToDetails
import com.example.sample.feature.home.navigation.HomeDestination
import com.example.sample.feature.home.navigation.homeScreen

/** Every feature registers its screens here through its `NavGraphBuilder.<feature>Screen()` extension. */
@Composable
fun SampleNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        homeScreen(onTitleClick = navController::navigateToDetails)
        detailsScreen(
            onBack = navController::popBackStack,
            onTitleClick = navController::navigateToDetails,
        )
    }
}
