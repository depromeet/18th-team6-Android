package configurations

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

private const val DETEKT_RULES_PROJECT_PATH = ":tools:detekt-rules"

internal fun Project.configureRootLint() {
    configureLint()

    subprojects {
        configureLint()
    }
}

private fun Project.configureLint() {
    if (path.startsWith(":tools")) {
        return
    }

    pluginManager.apply("io.gitlab.arturbosch.detekt")
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")

    configureCustomDetektRules()
    configureDetekt()
}

private fun Project.configureCustomDetektRules() {
    val detektRulesProject = rootProject.findProject(DETEKT_RULES_PROJECT_PATH) ?: return

    dependencies.add("detektPlugins", detektRulesProject)
}

private fun Project.configureDetekt() {
    extensions.configure<DetektExtension>("detekt") {
        baseline = file("detekt-baseline.xml")
        buildUponDefaultConfig = true
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        source.setFrom(
            if (this@configureDetekt == rootProject) {
                files("build.gradle.kts", "settings.gradle.kts")
            } else {
                files("src", "build.gradle.kts")
            },
        )
    }

    tasks.withType<Detekt>().configureEach {
        include("**/*.kt", "*.gradle.kts")
        exclude("**/build/**")
    }
}
