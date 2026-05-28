plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
    alias(libs.plugins.obrit.android.orbit)
}

dependencies {
    implementation(projects.android.core.ui)
    implementation(projects.android.core.designsystem)
    implementation(projects.shared.designSystem)
    implementation(projects.shared.data)
    api(projects.shared.model)
}
