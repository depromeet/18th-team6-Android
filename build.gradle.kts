plugins {
    alias(libs.plugins.obrit.root)

    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ktlint) apply false
}

dependencies {
    ktlintRuleset(libs.compose.rules.ktlint)
}

subprojects {
    if (!path.startsWith(":tools")) {
        dependencies {
            add("ktlintRuleset", rootProject.libs.compose.rules.ktlint)
        }
    }
}
