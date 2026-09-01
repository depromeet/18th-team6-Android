plugins {
    alias(libs.plugins.obrit.android.application)
    alias(libs.plugins.obrit.android.compose)
    alias(libs.plugins.obrit.android.koin)
    alias(libs.plugins.obrit.android.navigation3)
}

dependencies {
    implementation(projects.shared)
    implementation(projects.android.feature.agent)
    implementation(projects.android.feature.detail)
    implementation(projects.android.feature.register)
    implementation(projects.android.feature.home)
    implementation(projects.android.feature.notification)
    implementation(projects.android.core.designsystem)
    implementation(projects.android.core.ui)

    testImplementation(libs.kotlin.test)
}
