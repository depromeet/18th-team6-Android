plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    compileOnly(libs.detekt.api)
    compileOnly(libs.kotlin.compilerEmbeddable)
}
