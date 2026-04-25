package extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal val Project.catalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.versionInt(alias: String): Int =
    catalog
        .findVersion(alias)
        .get()
        .requiredVersion
        .toInt()

internal fun Project.versionString(alias: String): String =
    catalog
        .findVersion(alias)
        .get()
        .requiredVersion

internal fun Project.androidApplication(configure: ApplicationExtension.() -> Unit) {
    extensions.configure<ApplicationExtension>(configure)
}

internal fun Project.androidLibrary(configure: LibraryExtension.() -> Unit) {
    extensions.configure<LibraryExtension>(configure)
}

internal fun Project.kotlinAndroid(configure: KotlinAndroidProjectExtension.() -> Unit) {
    extensions.configure<KotlinAndroidProjectExtension>(configure)
}

internal fun Project.kotlinMultiplatform(configure: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure<KotlinMultiplatformExtension>(configure)
}
