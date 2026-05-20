package plugins

import extensions.catalog
import extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryNavigation3Plugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                implementation(catalog.findLibrary("androidx-navigation3-runtime").get())
                implementation(catalog.findLibrary("androidx-navigation3-ui").get())
                implementation(catalog.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
                implementation(catalog.findLibrary("kotlinx-serialization-core").get())
            }
        }
    }
}
