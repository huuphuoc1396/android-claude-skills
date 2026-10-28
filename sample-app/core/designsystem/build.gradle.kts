plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.sample.android.compose)
}

android {
    namespace = "com.example.sample.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
