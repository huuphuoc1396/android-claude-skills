package com.example.sample.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.sample.core.designsystem.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val inter = GoogleFont("Inter")

/** Inter, downloaded through Google Play Services; the system font is used until it is available. */
private val InterFamily = FontFamily(
    listOf(FontWeight.Light, FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold)
        .map { weight -> Font(googleFont = inter, fontProvider = provider, weight = weight) },
)

private val base = Typography()

private fun TextStyle.inter() = copy(fontFamily = InterFamily)

internal val SampleTypography = Typography(
    displayLarge = base.displayLarge.inter(),
    displayMedium = base.displayMedium.inter(),
    displaySmall = base.displaySmall.inter(),
    headlineLarge = base.headlineLarge.inter(),
    headlineMedium = base.headlineMedium.inter(),
    // Home heading: Inter 700 26.
    headlineSmall = base.headlineSmall.inter().copy(fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 31.5.sp),
    titleLarge = base.titleLarge.inter(),
    // Section headings: Inter 700 18.
    titleMedium = base.titleMedium.inter().copy(fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 22.sp),
    titleSmall = base.titleSmall.inter(),
    // Tab labels: Inter 400 16.
    bodyLarge = base.bodyLarge.inter().copy(fontSize = 16.sp, lineHeight = 19.sp),
    // Search placeholder: Inter 500 15.
    bodyMedium = base.bodyMedium.inter().copy(fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 18.sp),
    bodySmall = base.bodySmall.inter(),
    labelLarge = base.labelLarge.inter(),
    labelMedium = base.labelMedium.inter(),
    labelSmall = base.labelSmall.inter(),
)
