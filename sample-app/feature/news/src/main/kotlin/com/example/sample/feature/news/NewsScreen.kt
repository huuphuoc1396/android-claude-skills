package com.example.sample.feature.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sample.core.data.model.Article
import com.example.sample.core.designsystem.component.SampleEmptyState
import com.example.sample.core.designsystem.component.SampleErrorState
import com.example.sample.core.designsystem.component.SampleLoadingState
import com.example.sample.core.designsystem.component.SampleTopAppBar
import com.example.sample.core.designsystem.theme.SampleTheme
import java.time.Instant

@Composable
internal fun NewsRoute(
    onArticleClick: (url: String) -> Unit,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NewsScreen(
        uiState = uiState,
        onRetry = viewModel::refresh,
        onArticleClick = onArticleClick,
    )
}

@Composable
internal fun NewsScreen(
    uiState: NewsUiState,
    onRetry: () -> Unit,
    onArticleClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { SampleTopAppBar(title = stringResource(R.string.feature_news_title)) },
    ) { padding ->
        val contentModifier = Modifier.padding(padding)
        when (uiState) {
            NewsUiState.Loading -> SampleLoadingState(contentModifier)
            NewsUiState.Empty -> SampleEmptyState(stringResource(R.string.feature_news_empty), contentModifier)
            NewsUiState.Error -> SampleErrorState(
                message = stringResource(R.string.feature_news_error),
                onRetry = onRetry,
                retryLabel = stringResource(R.string.feature_news_retry),
                modifier = contentModifier,
            )
            is NewsUiState.Success -> ArticleList(uiState.articles, onArticleClick, contentModifier)
        }
    }
}

@Composable
private fun ArticleList(
    articles: List<Article>,
    onArticleClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(articles, key = { it.id }) { article ->
            ArticleItem(article, onClick = { onArticleClick(article.url) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun ArticleItem(
    article: Article,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(article.newsSite, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(article.title, style = MaterialTheme.typography.titleMedium)
        Text(
            article.summary,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun NewsScreenPreview() {
    SampleTheme {
        NewsScreen(
            uiState = NewsUiState.Success(
                listOf(
                    Article(
                        id = 1,
                        title = "Starship completes orbital test",
                        summary = "The vehicle reached orbit and splashed down on target.",
                        url = "https://example.com",
                        newsSite = "SpaceNews",
                        publishedAt = Instant.parse("2026-01-01T00:00:00Z"),
                    ),
                ),
            ),
            onRetry = {},
            onArticleClick = {},
        )
    }
}
