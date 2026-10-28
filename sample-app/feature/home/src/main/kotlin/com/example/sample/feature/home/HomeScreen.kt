package com.example.sample.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sample.core.data.model.Category
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.designsystem.component.SampleErrorState
import com.example.sample.core.designsystem.component.SampleLoadingState
import com.example.sample.core.designsystem.component.SamplePoster
import com.example.sample.core.designsystem.theme.SampleColors
import com.example.sample.core.designsystem.theme.SampleGradients
import com.example.sample.core.designsystem.theme.SampleTheme

private val SideMargin = 30.dp
private val HeaderHeight = 522.dp

// Unselected tabs and the search placeholder are white at reduced opacity (estimated from the screenshot).
private const val DimmedAlpha = 0.6f

@Composable
internal fun HomeRoute(
    onTitleClick: (Title) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onCategorySelected = viewModel::selectCategory,
        onRetry = viewModel::retry,
        onTitleClick = onTitleClick,
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onCategorySelected: (Category) -> Unit,
    onRetry: () -> Unit,
    onTitleClick: (Title) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SampleGradients.background),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(HeaderHeight)
                .background(SampleGradients.header),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item { Header() }
            item {
                CategoryTabs(
                    selected = uiState.selectedCategory,
                    onSelected = onCategorySelected,
                    modifier = Modifier.padding(top = 31.dp),
                )
            }
            content(uiState.content, onRetry, onTitleClick)
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(start = SideMargin, end = SideMargin, top = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = stringResource(R.string.feature_home_heading),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            ProfileAvatar(Modifier.padding(start = 16.dp, top = 7.dp))
        }
        HomeSearchField(Modifier.padding(top = 17.dp))
    }
}

/** No action yet: profile is out of scope (AC-12). */
@Composable
private fun ProfileAvatar(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_profile),
        contentDescription = stringResource(R.string.feature_home_profile),
        modifier = modifier
            .size(40.dp)
            .shadow(4.dp, CircleShape)
            .border(BorderStroke(2.dp, Color.White), CircleShape)
            .clip(CircleShape),
    )
}

/** Drawn as in the design but not interactive until search is built. */
@Composable
private fun HomeSearchField(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(SampleColors.SearchFill)
            .padding(start = 21.dp, end = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.feature_home_search),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = DimmedAlpha),
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun CategoryTabs(
    selected: Category,
    onSelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            // The selected pill sits 3 dp inside the side margin; its 14 dp padding aligns the labels.
            .padding(horizontal = 33.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Category.entries.forEach { category ->
            val isSelected = category == selected
            Text(
                text = stringResource(category.labelRes()),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.White.copy(alpha = DimmedAlpha),
                modifier = Modifier
                    .clip(RoundedCornerShape(70.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent)
                    .clickable { onSelected(category) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}

private fun LazyListScope.content(
    content: HomeContent,
    onRetry: () -> Unit,
    onTitleClick: (Title) -> Unit,
) {
    when (content) {
        HomeContent.Loading -> item { SampleLoadingState(Modifier.height(240.dp)) }
        HomeContent.Error -> item {
            SampleErrorState(
                message = stringResource(R.string.feature_home_error),
                onRetry = onRetry,
                retryLabel = stringResource(R.string.feature_home_retry),
                modifier = Modifier.height(240.dp),
            )
        }
        is HomeContent.Success -> {
            if (content.featured.isNotEmpty()) {
                item { PosterRow(content.featured, onTitleClick, Modifier.padding(top = 37.dp)) }
            }
            if (content.popular.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.feature_home_popular),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = SideMargin, end = SideMargin, top = 23.dp),
                    )
                }
                item { PosterRow(content.popular, onTitleClick, Modifier.padding(top = 16.dp)) }
            }
        }
    }
}

@Composable
private fun PosterRow(
    titles: List<Title>,
    onTitleClick: (Title) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = SideMargin),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(titles, key = { "${it.mediaType}-${it.id}" }) { title ->
            SamplePoster(
                imageUrl = title.posterUrl,
                contentDescription = title.name,
                onClick = { onTitleClick(title) },
            )
        }
    }
}

private fun Category.labelRes(): Int = when (this) {
    Category.Films -> R.string.feature_home_tab_films
    Category.Series -> R.string.feature_home_tab_series
    Category.Anime -> R.string.feature_home_tab_anime
    Category.Novelas -> R.string.feature_home_tab_novelas
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val titles = (1L..5L).map { Title(id = it, mediaType = MediaType.Movie, name = "Title $it", posterUrl = null) }
    SampleTheme {
        HomeScreen(
            uiState = HomeUiState(Category.Films, HomeContent.Success(featured = titles, popular = titles)),
            onCategorySelected = {},
            onRetry = {},
            onTitleClick = {},
        )
    }
}
