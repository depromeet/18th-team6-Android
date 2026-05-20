plugins {
    alias(libs.plugins.obrit.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            api(projects.shared.model)
            implementation(projects.shared.network)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }
        iosMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
        }
    }
}
