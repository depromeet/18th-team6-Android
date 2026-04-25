package configurations

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

internal fun Project.configureRootLint() {
    configureLint()

    subprojects {
        configureLint()
    }
}

private fun Project.configureLint() {
    pluginManager.apply("io.gitlab.arturbosch.detekt")
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")

    configureDetekt()
}

private fun Project.configureDetekt() {
    extensions.configure<DetektExtension>("detekt") {
        baseline = file("detekt-baseline.xml")
        buildUponDefaultConfig = true
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        source.setFrom(files("src"))
    }

    tasks.withType<Detekt>().configureEach {
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**")
    }
}
