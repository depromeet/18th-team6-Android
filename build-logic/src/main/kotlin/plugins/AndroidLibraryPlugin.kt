package plugins

import configurations.configureAndroidLibrary
import configurations.configureKotlinAndroid
import extensions.catalog
import extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")

            configureKotlinAndroid()
            configureAndroidLibrary()

            dependencies {
                implementation(catalog.findLibrary("androidx-core-ktx").get())
            }
        }
    }
}
