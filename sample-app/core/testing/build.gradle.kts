plugins {
    alias(libs.plugins.sample.android.library)
}

android {
    namespace = "com.example.sample.core.testing"
}

dependencies {
    api(project(":core:data"))
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}
