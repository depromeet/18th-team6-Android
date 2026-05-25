plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
    alias(libs.plugins.obrit.android.orbit)
}

dependencies {
    implementation(projects.android.core.designsystem)
    implementation(projects.android.core.ui)
    implementation(projects.shared.designSystem)
}
