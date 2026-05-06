import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.obrit.kotlin.multiplatform)
}

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(projects.shared.designSystem)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)

            api(projects.shared.designSystem)
            implementation(projects.shared.network)
            implementation(projects.shared.data)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
