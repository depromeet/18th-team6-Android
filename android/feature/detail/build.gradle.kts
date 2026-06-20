plugins {
    alias(libs.plugins.obrit.android.library)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
    alias(libs.plugins.obrit.android.orbit)
}

dependencies {
    implementation(projects.shared.data)
    implementation(projects.android.core.designsystem)
    implementation(projects.android.core.ui)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
