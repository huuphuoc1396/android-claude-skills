plugins {
    alias(libs.plugins.sample.android.feature)
}

android {
    namespace = "com.example.sample.feature.details"
}

dependencies {
    implementation(libs.coil.compose)
}
