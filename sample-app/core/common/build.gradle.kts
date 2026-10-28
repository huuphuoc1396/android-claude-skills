plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.sample.hilt)
}

android {
    namespace = "com.example.sample.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.android)
}
