plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("obritRootPlugin") {
            id = "com.obrit.plugin.root"
            implementationClass = "plugins.ObritRootPlugin"
        }
        register("androidApplicationPlugin") {
            id = "com.obrit.plugin.android.application"
            implementationClass = "plugins.AndroidApplicationPlugin"
        }
        register("androidLibraryPlugin") {
            id = "com.obrit.plugin.android.library"
            implementationClass = "plugins.AndroidLibraryPlugin"
        }
        register("androidLibraryComposePlugin") {
            id = "com.obrit.plugin.android.library.compose"
            implementationClass = "plugins.AndroidLibraryComposePlugin"
        }
        register("kotlinMultiplatformPlugin") {
            id = "com.obrit.plugin.kotlin.multiplatform"
            implementationClass = "plugins.KotlinMultiplatformPlugin"
        }
        register("androidLibraryKoinPlugin") {
            id = "com.obrit.plugin.android.library.koin"
            implementationClass = "plugins.AndroidLibraryKoinPlugin"
        }
        register("androidLibraryOrbitPlugin") {
            id = "com.obrit.plugin.android.library.orbit"
            implementationClass = "plugins.AndroidLibraryOrbitPlugin"
        }
        register("androidLibraryNavigation3Plugin") {
            id = "com.obrit.plugin.android.library.navigation3"
            implementationClass = "plugins.AndroidLibraryNavigation3Plugin"
        }
    }
}
