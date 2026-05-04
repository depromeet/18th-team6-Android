package plugins

import extensions.catalog
import extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryOrbitPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            dependencies {
                implementation(catalog.findLibrary("orbit-core").get())
                implementation(catalog.findLibrary("orbit-viewmodel").get())
                implementation(catalog.findLibrary("orbit-compose").get())
            }
        }
    }
}
