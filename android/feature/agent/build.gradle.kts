plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
    alias(libs.plugins.obrit.android.orbit)
}

dependencies {
    api(projects.shared.model)
    implementation(projects.shared.data)
    implementation(projects.android.core.ui)
}
