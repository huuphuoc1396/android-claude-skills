plugins {
    alias(libs.plugins.sample.android.application)
    alias(libs.plugins.sample.android.compose)
    alias(libs.plugins.sample.hilt)
}

android {
    namespace = "com.example.sample"

    defaultConfig {
        applicationId = "com.example.sample"
        versionCode = 1
        versionName = "0.1.0"
    }

    // Lets Android 13+ offer English and Portuguese in the per-app language settings.
    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:home"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
