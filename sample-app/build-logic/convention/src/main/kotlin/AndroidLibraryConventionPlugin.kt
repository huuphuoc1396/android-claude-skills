import com.android.build.api.dsl.LibraryExtension
import com.example.sample.libs
import com.example.sample.versionInt
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                compileSdk = libs.versionInt("compileSdk")
                defaultConfig {
                    minSdk = libs.versionInt("minSdk")
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }

            dependencies {
                add("testImplementation", libs.findLibrary("junit").get())
            }
        }
    }
}
