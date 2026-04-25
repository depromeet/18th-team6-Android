import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.obrit.kotlin.multiplatform)
}

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(projects.shared.network)
            implementation(projects.shared.data)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
