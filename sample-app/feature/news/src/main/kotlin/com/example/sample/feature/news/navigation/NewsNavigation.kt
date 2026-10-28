package com.example.sample.feature.news.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sample.feature.news.NewsRoute
import kotlinx.serialization.Serializable

@Serializable
data object NewsDestination

fun NavGraphBuilder.newsScreen(onArticleClick: (url: String) -> Unit) {
    composable<NewsDestination> {
        NewsRoute(onArticleClick = onArticleClick)
    }
}
