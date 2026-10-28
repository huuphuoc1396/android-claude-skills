plugins {
    alias(libs.plugins.sample.android.library)
    alias(libs.plugins.sample.hilt)
}

android {
    namespace = "com.example.sample.core.datastore"
}

dependencies {
    implementation(project(":core:common"))
    api(libs.androidx.datastore.preferences)

    testImplementation(libs.kotlinx.coroutines.test)
}
