import java.io.File
import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.obrit.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

private val networkBaseUrlLocalPropertyName = "network.base.url"

private val networkLocalPropertiesFile = rootProject.layout.projectDirectory.file("local.properties")
private val generatedNetworkConfigDirectory =
    layout.buildDirectory.dir("generated/source/networkConfig/commonMain/kotlin")

private val generateNetworkConfig by tasks.registering(GenerateNetworkConfigTask::class) {
    localPropertiesFile.set(networkLocalPropertiesFile)
    networkBaseUrlPropertyName.set(networkBaseUrlLocalPropertyName)
    outputDirectory.set(generatedNetworkConfigDirectory)
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(generateNetworkConfig)

            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(project.dependencies.platform(libs.ktor.bom))
                implementation(projects.shared.model)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

abstract class GenerateNetworkConfigTask : DefaultTask() {
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val localPropertiesFile: RegularFileProperty

    @get:Input
    abstract val networkBaseUrlPropertyName: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val propertyName = networkBaseUrlPropertyName.get()
        val propertiesFile = localPropertiesFile.get().asFile
        val networkBaseUrl =
            loadProperties(propertiesFile)
                .getProperty(propertyName)
                ?.trim()
                .orEmpty()

        check(networkBaseUrl.isNotBlank()) {
            "Missing '$propertyName' in ${propertiesFile.path}. " +
                "Add '$propertyName=https://example.com/' to local.properties."
        }

        val outputFile =
            outputDirectory
                .file("com/obrit/obrit/shared/network/config/NetworkBuildConfig.kt")
                .get()
                .asFile

        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package com.obrit.obrit.shared.network.config

            internal const val NETWORK_BASE_URL = ${networkBaseUrl.toKotlinStringLiteral()}
            """.trimIndent() + "\n",
        )
    }

    private fun loadProperties(file: File): Properties =
        Properties().apply {
            if (file.isFile) {
                file.inputStream().use { input -> load(input) }
            }
        }

    private fun String.toKotlinStringLiteral(): String =
        buildString(length + 2) {
            append('"')
            this@toKotlinStringLiteral.forEach { char ->
                when (char) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '$' -> {
                        append('\\')
                        append('$')
                    }
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(char)
                }
            }
            append('"')
        }
}
