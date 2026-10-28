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
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:details"))
    implementation(project(":feature:home"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:search"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
