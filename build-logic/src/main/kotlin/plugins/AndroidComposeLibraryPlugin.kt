package plugins

import configurations.configureAndroidLibrary
import configurations.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidComposeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            configureKotlinAndroid()
            configureAndroidLibrary(enableCompose = true)
        }
    }
}
