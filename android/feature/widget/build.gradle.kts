plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
}

dependencies {
    implementation(projects.shared.designSystem)
    implementation(projects.shared.data)
    implementation(libs.androidx.glance.appwidget)
}
