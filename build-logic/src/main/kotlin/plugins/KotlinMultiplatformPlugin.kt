package plugins

import configurations.configureAndroidMultiplatformLibrary
import configurations.configureKotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.library")

            configureKotlinMultiplatform()
            configureAndroidMultiplatformLibrary()
        }
    }
}
