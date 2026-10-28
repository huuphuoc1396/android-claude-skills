package com.example.sample.feature.details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sample.core.data.model.Title
import com.example.sample.feature.details.DetailsRoute
import kotlinx.serialization.Serializable

/** [mediaType] is a [com.example.sample.core.data.model.MediaType] name. */
@Serializable
data class DetailsDestination(val id: Long, val mediaType: String)

/** Every call adds a new entry, so opening a recommendation stacks on top of the current title (AC-13). */
fun NavController.navigateToDetails(title: Title) {
    navigate(DetailsDestination(id = title.id, mediaType = title.mediaType.name))
}

fun NavGraphBuilder.detailsScreen(
    onBack: () -> Unit,
    onTitleClick: (Title) -> Unit,
) {
    composable<DetailsDestination> {
        DetailsRoute(onBack = onBack, onTitleClick = onTitleClick)
    }
}
