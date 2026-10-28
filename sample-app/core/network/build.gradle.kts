plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.sample.hilt)
}

android {
    namespace = "com.example.sample.core.network"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp3.logging)
}
