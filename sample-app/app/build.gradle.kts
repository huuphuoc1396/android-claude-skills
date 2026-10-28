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

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:news"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
