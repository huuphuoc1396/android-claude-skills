package com.example.sample.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sample.core.data.model.Title
import com.example.sample.feature.search.SearchRoute
import kotlinx.serialization.Serializable

@Serializable
data class SearchDestination(val query: String)

fun NavController.navigateToSearch(query: String) {
    navigate(SearchDestination(query))
}

fun NavGraphBuilder.searchScreen(
    onBack: () -> Unit,
    onTitleClick: (Title) -> Unit,
) {
    composable<SearchDestination> {
        SearchRoute(onBack = onBack, onTitleClick = onTitleClick)
    }
}
