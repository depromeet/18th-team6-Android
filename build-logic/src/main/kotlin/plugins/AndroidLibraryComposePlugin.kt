package plugins

import extensions.catalog
import extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                implementation(platform(catalog.findLibrary("androidx-compose-bom").get()))
                implementation(catalog.findLibrary("androidx-compose-ui").get())
                implementation(catalog.findLibrary("androidx-activity-compose").get())
                implementation(catalog.findLibrary("androidx-compose-material3").get())
                implementation(catalog.findLibrary("androidx-compose-runtime").get())
                implementation(catalog.findLibrary("androidx-compose-foundation").get())
                implementation(catalog.findLibrary("androidx-compose-animation").get())
                implementation(catalog.findLibrary("androidx-compose-ui-tooling").get())
                implementation(catalog.findLibrary("androidx-compose-ui-tooling-preview").get())
            }
        }
    }
}
