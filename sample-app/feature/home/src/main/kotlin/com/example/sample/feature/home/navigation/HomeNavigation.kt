package com.example.sample.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sample.core.data.model.Title
import com.example.sample.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination

fun NavGraphBuilder.homeScreen(
    onTitleClick: (Title) -> Unit,
    onSearch: (query: String) -> Unit,
) {
    composable<HomeDestination> {
        HomeRoute(onTitleClick = onTitleClick, onSearch = onSearch)
    }
}
