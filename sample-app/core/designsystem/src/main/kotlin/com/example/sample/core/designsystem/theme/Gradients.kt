package com.example.sample.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Gradients from the design; stops match design.md "Visual tokens". */
object SampleGradients {
    /** Screen background, top to bottom. */
    val background: Brush = Brush.verticalGradient(0f to Slate, 0.65f to Night)

    /** Purple glow behind the Home header, fading out downwards. */
    val header: Brush = Brush.verticalGradient(listOf(Purple, Color.Transparent))

    /** Primary buttons and selected accents, left to right. */
    val accent: Brush = Brush.horizontalGradient(listOf(Purple, PurpleDark))
}
