import java.util.Properties

plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.sample.hilt)
}

// The key lives in the gitignored local.properties; an empty key makes every TMDB request fail with 401.
val tmdbApiKey: String = Properties()
    .apply { rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load) }
    .getProperty("TMDB_API_KEY", "")

android {
    namespace = "com.example.sample.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp3.logging)
}
