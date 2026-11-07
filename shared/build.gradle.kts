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
            export(projects.shared.model)
            export(projects.shared.data)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)

            api(projects.shared.designSystem)
            api(projects.shared.model)
            api(projects.shared.data)
            implementation(projects.shared.network)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
