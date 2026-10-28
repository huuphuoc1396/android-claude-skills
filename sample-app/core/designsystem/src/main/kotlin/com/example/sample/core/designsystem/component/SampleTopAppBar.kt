package com.example.sample.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
    )
}
