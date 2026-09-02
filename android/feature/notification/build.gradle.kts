plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
}

dependencies {
    implementation(projects.android.core.designsystem)
    implementation(projects.shared.designSystem)
}
