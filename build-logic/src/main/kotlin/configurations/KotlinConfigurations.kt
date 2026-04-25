package configurations

import extensions.kotlinAndroid
import extensions.kotlinMultiplatform
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal fun Project.configureKotlinAndroid() {
    kotlinAndroid {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

internal fun Project.configureKotlinMultiplatform() {
    kotlinMultiplatform {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }

        iosArm64()
        iosSimulatorArm64()
    }
}
