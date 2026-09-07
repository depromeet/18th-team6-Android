plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.koin)
}

android {
    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
