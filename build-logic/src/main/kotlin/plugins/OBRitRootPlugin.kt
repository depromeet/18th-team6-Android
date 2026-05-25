package plugins

import configurations.configureRootLint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import tasks.GenerateIosDesignTokensTask

class OBRitRootPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureRootLint()
        target.registerGenerateIosDesignTokensTask()
    }

    private fun Project.registerGenerateIosDesignTokensTask() {
        val tokenRoot = layout.projectDirectory.dir(
            "shared/design-system/src/commonMain/kotlin/com/obrit/obrit/shared/designsystem/tokens",
        )
        val outputFile = layout.projectDirectory.file(
            "iosApp/iosApp/DesignSystem/Foundation/OBRitDesignTokens.swift",
        )

        tasks.register<GenerateIosDesignTokensTask>("generateIosDesignTokens") {
            this.tokenRoot.set(tokenRoot)
            this.outputFile.set(outputFile)
        }
    }
}
