package com.iwbi.gradleplugins.ktx

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.getLibVersion(library: String): String = libs.findVersion(library).get().requiredVersion

internal fun Project.configureAndroid() {
    val extension = when {
        plugins.hasPlugin("com.android.application") -> {
            extensions.getByType<ApplicationExtension>()
        }
        plugins.hasPlugin("com.android.library") -> {
            extensions.getByType<LibraryExtension>()
        }
        else -> error("No supported android plugin found")
    }

    extension.apply {
        compileSdk = getLibVersion("android-compileSdk").toInt()
        buildToolsVersion = getLibVersion("android-buildTools")

        defaultConfig {
            minSdk = getLibVersion("android-minSdk").toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        app {
            defaultConfig {
                targetSdk = getLibVersion("android-targetSdk").toInt()
            }
        }

        val jvmTarget = getLibVersion("android-jvmTarget")
        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(jvmTarget)
            targetCompatibility = JavaVersion.toVersion(jvmTarget)
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }
}

private fun CommonExtension<*, *, *, *, *, *>.app(block: ApplicationExtension.() -> Unit) {
    if (this is ApplicationExtension) {
        block()
    }
}