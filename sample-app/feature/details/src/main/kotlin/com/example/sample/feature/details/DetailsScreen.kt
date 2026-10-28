package com.example.sample.feature.details

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.sample.core.data.model.CastMember
import com.example.sample.core.data.model.MediaType
import com.example.sample.core.data.model.Title
import com.example.sample.core.data.model.TitleDetails
import com.example.sample.core.designsystem.component.SampleErrorState
import com.example.sample.core.designsystem.component.SampleLoadingState
import com.example.sample.core.designsystem.component.SamplePoster
import com.example.sample.core.designsystem.component.SamplePrimaryButton
import com.example.sample.core.designsystem.theme.SampleColors
import com.example.sample.core.designsystem.theme.SampleTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

private val SideMargin = 30.dp
private val ArtworkHeight = 493.dp

// The title block starts where the artwork's fade is complete.
private val ContentTop = 376.dp

// The artwork is clear down to 263 dp and fully faded at 415 dp (design.md: show backdrop).
private val ArtworkFade = Brush.verticalGradient(
    0.533f to Color.Transparent,
    0.842f to SampleColors.HeaderFade,
)

@Composable
internal fun DetailsRoute(
    onBack: () -> Unit,
    onTitleClick: (Title) -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailsScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onTitleClick = onTitleClick,
        onFavoriteChange = viewModel::setFavorite,
    )
}

@Composable
internal fun DetailsScreen(
    uiState: DetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onTitleClick: (Title) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val trailerError = stringResource(R.string.feature_details_trailer_error)
    val refreshFailed = (uiState as? DetailsUiState.Success)?.refreshFailed == true
    val refreshFailedMessage = stringResource(R.string.feature_details_refresh_failed)
    LaunchedEffect(refreshFailed) {
        if (refreshFailed) snackbarHostState.showSnackbar(refreshFailedMessage)
    }
    val openTrailer: (String) -> Unit = { url ->
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (e: ActivityNotFoundException) {
            scope.launch { snackbarHostState.showSnackbar(trailerError) }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SampleColors.HeaderFade),
    ) {
        when (uiState) {
            DetailsUiState.Loading -> SampleLoadingState()
            is DetailsUiState.Error -> SampleErrorState(
                message = stringResource(
                    if (uiState.offline) R.string.feature_details_offline else R.string.feature_details_error,
                ),
                onRetry = onRetry,
                retryLabel = stringResource(R.string.feature_details_retry),
            )
            is DetailsUiState.Success -> DetailsContent(uiState.details, uiState.offlineNotice, onTitleClick, openTrailer)
        }
        BackButton(onBack)
        if (uiState is DetailsUiState.Success) {
            FavoriteButton(
                isFavorite = uiState.isFavorite,
                onClick = { onFavoriteChange(!uiState.isFavorite) },
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 8.dp, end = SideMargin)
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite),
            contentDescription = stringResource(
                if (isFavorite) R.string.feature_details_favorite_remove else R.string.feature_details_favorite_add,
            ),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .padding(start = 14.dp, top = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            // Keeps "Voltar" readable when the page scrolls under it; matches the favorite button.
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .clickable(onClick = onBack)
            .padding(start = 6.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = stringResource(R.string.feature_details_back),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Composable
private fun DetailsContent(
    details: TitleDetails,
    offlineNotice: Boolean,
    onTitleClick: (Title) -> Unit,
    onTrailerClick: (String) -> Unit,
) {
    Box(Modifier.verticalScroll(rememberScrollState())) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(ArtworkHeight)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            AsyncImage(
                model = details.artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(ArtworkFade),
            )
        }
        Column(
            modifier = Modifier
                .padding(top = ContentTop)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
        ) {
            TitleBlock(details, Modifier.padding(horizontal = SideMargin))
            HorizontalDivider(
                color = Color.Black,
                modifier = Modifier.padding(start = SideMargin, end = SideMargin, top = 20.dp),
            )
            if (offlineNotice) {
                Text(
                    text = stringResource(R.string.feature_details_cached_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = SampleColors.TextTertiary,
                    modifier = Modifier.padding(start = SideMargin, end = SideMargin, top = 16.dp),
                )
            }
            details.synopsis?.let { synopsis ->
                Text(
                    text = synopsis,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp, lineHeight = 17.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = SideMargin, end = SideMargin, top = 21.dp),
                )
            }
            details.trailerUrl?.let { url ->
                SamplePrimaryButton(
                    label = stringResource(R.string.feature_details_trailer),
                    onClick = { onTrailerClick(url) },
                    icon = painterResource(R.drawable.ic_play),
                    modifier = Modifier.padding(start = SideMargin, end = SideMargin, top = 30.dp),
                )
            }
            if (details.cast.isNotEmpty()) {
                val castTop = if (details.trailerUrl != null) 46.dp else 30.dp
                SectionHeading(stringResource(R.string.feature_details_cast), Modifier.padding(top = castTop))
                CastRow(details.cast)
            }
            if (details.genres.isNotEmpty()) {
                SectionHeading(stringResource(R.string.feature_details_genres), Modifier.padding(top = 23.dp))
                Genres(details.genres)
            }
            if (details.recommendations.isNotEmpty()) {
                SectionHeading(stringResource(R.string.feature_details_recommendations), Modifier.padding(top = 41.dp))
                LazyRow(
                    modifier = Modifier.padding(top = 20.dp),
                    contentPadding = PaddingValues(horizontal = SideMargin),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(details.recommendations, key = { "${it.mediaType}-${it.id}" }) { title ->
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
}

@Composable
private fun TitleBlock(details: TitleDetails, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        details.ratingPercent?.let { RatingRing(it, Modifier.padding(end = 16.dp)) }
        Column {
            val year = details.releaseDate?.year
            Text(
                text = if (year != null) "${details.name} ($year)" else details.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp, lineHeight = 28.sp),
                color = Color.White,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 4.dp),
            ) {
                val locale = LocalConfiguration.current.locales[0]
                details.releaseDate?.let { date ->
                    val country = details.releaseCountry?.let { " ($it)" }.orEmpty()
                    MetaText(formatReleaseDate(date, locale) + country)
                }
                if (details.releaseDate != null && details.runtimeMinutes != null) {
                    MetaText("•")
                }
                details.runtimeMinutes?.let { minutes ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                        )
                        MetaText(formatRuntime(minutes), Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = SampleColors.TextTertiary,
        modifier = modifier,
    )
}

/** TMDB score as a pink arc around the percentage. */
@Composable
private fun RatingRing(percent: Int, modifier: Modifier = Modifier) {
    val track = MaterialTheme.colorScheme.surface
    val arc = MaterialTheme.colorScheme.secondary
    val description = stringResource(R.string.feature_details_rating, percent)
    Box(
        modifier = modifier
            .size(57.dp)
            .clearAndSetSemantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(track)
            drawArc(arc, startAngle = -90f, sweepAngle = 360f * percent / 100f, useCenter = true)
            drawCircle(SampleColors.SurfaceDeep, radius = size.minDimension / 2 * 45f / 57f)
        }
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

@Composable
private fun SectionHeading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        modifier = modifier.padding(horizontal = SideMargin),
    )
}

@Composable
private fun CastRow(cast: List<CastMember>) {
    LazyRow(
        modifier = Modifier.padding(top = 20.dp),
        contentPadding = PaddingValues(horizontal = SideMargin),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(cast) { member -> CastItem(member) }
    }
}

@Composable
private fun CastItem(member: CastMember) {
    Column(
        modifier = Modifier.width(75.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = member.photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
        )
        Text(
            text = member.name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, lineHeight = 15.sp),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 7.dp),
        )
    }
}

@Composable
private fun Genres(genres: List<String>) {
    FlowRow(
        modifier = Modifier.padding(start = SideMargin, end = SideMargin, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        genres.forEach { genre ->
            Text(
                text = genre,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(34.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 17.dp, vertical = 6.5.dp),
            )
        }
    }
}

@Preview
@Composable
private fun DetailsScreenPreview() {
    SampleTheme {
        DetailsScreen(
            uiState = DetailsUiState.Success(
                TitleDetails(
                    id = 1,
                    mediaType = MediaType.Movie,
                    name = "Imperdoável",
                    releaseDate = LocalDate.of(2021, 12, 10),
                    releaseCountry = "BR",
                    ratingPercent = 45,
                    runtimeMinutes = 113,
                    synopsis = "Após cumprir pena por um crime violento, Ruth volta ao convívio na sociedade.",
                    artworkUrl = null,
                    cast = List(5) { CastMember("Sandra Bullock", null) },
                    genres = listOf("Drama", "Thriller"),
                    recommendations = List(3) { Title(it.toLong(), MediaType.Tv, "Lucifer", null) },
                    trailerUrl = "https://www.youtube.com/watch?v=example",
                ),
                isFavorite = true,
            ),
            onBack = {},
            onRetry = {},
            onTitleClick = {},
            onFavoriteChange = {},
        )
    }
}
