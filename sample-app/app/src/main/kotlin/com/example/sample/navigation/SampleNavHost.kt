package com.example.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.sample.feature.news.navigation.NewsDestination
import com.example.sample.feature.news.navigation.newsScreen

/** Every feature registers its screens here through its `NavGraphBuilder.<feature>Screen()` extension. */
@Composable
fun SampleNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val uriHandler = LocalUriHandler.current
    NavHost(
        navController = navController,
        startDestination = NewsDestination,
        modifier = modifier,
    ) {
        newsScreen(onArticleClick = uriHandler::openUri)
    }
}
