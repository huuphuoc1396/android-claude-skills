package com.example.sample.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sample.core.designsystem.theme.SampleTheme

/** Full-screen states shared by every screen. Use these instead of building loading/empty/error UI per feature. */
@Composable
fun SampleLoadingState(modifier: Modifier = Modifier) {
    CenteredColumn(modifier) {
        CircularProgressIndicator()
    }
}

@Composable
fun SampleEmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    CenteredColumn(modifier) {
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}

@Composable
fun SampleErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retryLabel: String = "Retry",
) {
    CenteredColumn(modifier) {
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text(retryLabel) }
    }
}

@Composable
private fun CenteredColumn(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Preview
@Composable
private fun SampleErrorStatePreview() {
    SampleTheme {
        SampleErrorState(message = "Something went wrong", onRetry = {})
    }
}
