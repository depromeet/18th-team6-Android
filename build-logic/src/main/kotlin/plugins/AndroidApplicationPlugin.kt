package plugins

import configurations.configureAndroidApplication
import configurations.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            configureKotlinAndroid()
            configureAndroidApplication()
        }
    }
}
