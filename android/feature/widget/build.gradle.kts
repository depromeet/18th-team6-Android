plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
}

dependencies {
    implementation(projects.shared.designSystem)
    implementation(libs.androidx.glance.appwidget)
}
