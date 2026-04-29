plugins {
    alias(libs.plugins.obrit.android.application)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
}

dependencies {
    implementation(projects.shared)
    implementation(projects.shared.data)
    implementation(projects.shared.network)
    implementation(projects.android.feature.agent)

    testImplementation(libs.kotlin.test)
}
