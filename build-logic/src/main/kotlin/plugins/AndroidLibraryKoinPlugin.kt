package plugins

import extensions.catalog
import extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryKoinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            dependencies {
                implementation(platform(catalog.findLibrary("koin-bom").get()))
                implementation(catalog.findLibrary("koin-android").get())
                implementation(catalog.findLibrary("koin-androidx-compose").get())
            }
        }
    }
}
