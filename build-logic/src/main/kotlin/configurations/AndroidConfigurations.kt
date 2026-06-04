package configurations

import extensions.androidApplication
import extensions.androidLibrary
import extensions.moduleNamespace
import extensions.versionInt
import extensions.versionString
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureAndroidApplication() {
    androidApplication {
        namespace = moduleNamespace
        compileSdk = versionInt("android-compileSdk")

        defaultConfig {
            applicationId = versionString("app-applicationId")
            minSdk = versionInt("android-minSdk")
            targetSdk = versionInt("android-targetSdk")
            versionCode = versionInt("app-versionCode")
            versionName = versionString("app-versionName")
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
            getByName("debug") {
                applicationIdSuffix = ".debug"
                versionNameSuffix = "-debug"
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        buildFeatures {
            compose = true
        }
    }
}

internal fun Project.configureAndroidLibrary(enableCompose: Boolean = true) {
    androidLibrary {
        namespace = moduleNamespace
        compileSdk = versionInt("android-compileSdk")

        defaultConfig {
            minSdk = versionInt("android-minSdk")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles("consumer-rules.pro")
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        buildFeatures {
            compose = enableCompose
        }
    }
}

internal fun Project.configureAndroidMultiplatformLibrary() {
    androidLibrary {
        namespace = moduleNamespace
        compileSdk = versionInt("android-compileSdk")

        defaultConfig {
            minSdk = versionInt("android-minSdk")
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}
