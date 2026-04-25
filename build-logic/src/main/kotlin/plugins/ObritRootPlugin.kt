package plugins

import configurations.configureRootLint
import org.gradle.api.Plugin
import org.gradle.api.Project

class ObritRootPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureRootLint()
    }
}
