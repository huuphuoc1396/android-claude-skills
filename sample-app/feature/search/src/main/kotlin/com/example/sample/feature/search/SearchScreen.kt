package com.example.sample.feature.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.designsystem.component.SampleEmptyState
import com.example.sample.core.designsystem.component.SampleErrorState
import com.example.sample.core.designsystem.component.SampleLoadingState
import com.example.sample.core.designsystem.component.SamplePoster
import com.example.sample.core.designsystem.component.SampleSearchField
import com.example.sample.core.designsystem.theme.SampleGradients
import com.example.sample.core.designsystem.theme.SampleTheme

private val SideMargin = 30.dp

@Composable
internal fun SearchRoute(
    onBack: () -> Unit,
    onTitleClick: (Title) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreen(
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::submit,
        onRetry = viewModel::retry,
        onTitleClick = onTitleClick,
    )
}

@Composable
internal fun SearchScreen(
    uiState: SearchUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onTitleClick: (Title) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SampleGradients.background)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = SideMargin, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.feature_search_back),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .padding(4.dp)
                    .size(24.dp),
            )
            SampleSearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                placeholder = stringResource(R.string.feature_search_hint),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        when (val results = uiState.results) {
            SearchResults.Loading -> SampleLoadingState()
            SearchResults.NoResults -> SampleEmptyState(stringResource(R.string.feature_search_no_results, uiState.query))
            SearchResults.Offline -> SampleErrorState(
                message = stringResource(R.string.feature_search_offline),
                onRetry = onRetry,
                retryLabel = stringResource(R.string.feature_search_retry),
            )
            SearchResults.Error -> SampleErrorState(
                message = stringResource(R.string.feature_search_error),
                onRetry = onRetry,
                retryLabel = stringResource(R.string.feature_search_retry),
            )
            is SearchResults.Found -> LazyVerticalGrid(
                columns = GridCells.Adaptive(124.dp),
                contentPadding = PaddingValues(start = SideMargin, end = SideMargin, top = 24.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(results.titles, key = { "${it.mediaType}-${it.id}" }) { title ->
                    SamplePoster(
                        imageUrl = title.posterUrl,
                        contentDescription = title.name,
                        onClick = { onTitleClick(title) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SampleTheme {
        SearchScreen(
            uiState = SearchUiState(
                query = "matrix",
                results = SearchResults.Found(List(4) { Title(it.toLong(), MediaType.Movie, "Matrix", null) }),
            ),
            onBack = {},
            onQueryChange = {},
            onSearch = {},
            onRetry = {},
            onTitleClick = {},
        )
    }
}
