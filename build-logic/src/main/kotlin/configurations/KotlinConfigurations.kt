package configurations

import extensions.kotlinAndroid
import extensions.kotlinMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

private const val JAVA_VERSION = 11

internal fun Project.configureKotlinAndroid() {
    kotlinAndroid {
        jvmToolchain(JAVA_VERSION)

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

internal fun Project.configureKotlinMultiplatform() {
    kotlinMultiplatform {
        jvmToolchain(JAVA_VERSION)

        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }

        iosArm64()
        iosSimulatorArm64()
    }
}
