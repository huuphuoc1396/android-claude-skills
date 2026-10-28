package com.example.sample.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sample.core.designsystem.theme.SampleGradients
import com.example.sample.core.designsystem.theme.SampleTheme

@Composable
internal fun OnboardingRoute(
    onContinue: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    OnboardingScreen(onContinue = { viewModel.completeOnboarding(onDone = onContinue) })
}

@Composable
internal fun OnboardingScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The artwork darkens from 31% up from the bottom to 63% (design.md: onboard background).
    val bottomShade = Brush.verticalGradient(
        0.31f to Color.Transparent,
        0.63f to MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
    )
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.img_onboarding_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(bottomShade),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 30.dp, end = 30.dp, bottom = 31.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.img_logo_tmdb),
                contentDescription = stringResource(R.string.feature_onboarding_logo),
                modifier = Modifier.size(width = 176.dp, height = 15.dp),
            )
            Text(
                text = AnnotatedString.fromHtml(stringResource(R.string.feature_onboarding_headline)),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, lineHeight = 39.sp),
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(top = 19.dp),
            )
            Text(
                text = stringResource(R.string.feature_onboarding_body),
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier.padding(top = 19.dp),
            )
            PrimaryButton(
                label = stringResource(R.string.feature_onboarding_continue),
                onClick = onContinue,
                modifier = Modifier.padding(top = 19.dp),
            )
        }
    }
}

@Composable
private fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(43.dp)
            .clip(RoundedCornerShape(60.dp))
            .background(SampleGradients.accent)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenPreview() {
    SampleTheme {
        OnboardingScreen(onContinue = {})
    }
}
