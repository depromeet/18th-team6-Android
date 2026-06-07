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

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}
