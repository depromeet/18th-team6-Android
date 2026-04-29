plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
    alias(libs.plugins.obrit.android.orbit)
    alias(libs.plugins.obrit.android.navigation3)
}

dependencies {
    implementation(projects.shared.data)
    implementation(projects.android.core.ui)
}
