package com.iwbi.gradleplugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.lint.AndroidLintAnalysisTask
import com.android.build.gradle.internal.lint.LintModelWriterTask
import com.android.build.gradle.internal.lint.VariantInputs
import java.lang.reflect.Field
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.tooling.core.withClosure

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