plugins {
    alias(libs.plugins.obrit.android.application)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.koin.android)
    implementation(projects.shared)
    implementation(projects.shared.data)
    implementation(projects.shared.network)
    implementation(projects.android.feature.agent)
    testImplementation(libs.kotlin.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
