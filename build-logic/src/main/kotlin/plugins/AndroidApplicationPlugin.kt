package plugins

import configurations.configureAndroidApplication
import configurations.configureKotlinAndroid
import extensions.catalog
import extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            configureKotlinAndroid()
            configureAndroidApplication()

            dependencies {
                implementation(catalog.findLibrary("androidx-core-ktx").get())
            }
        }
    }
}
