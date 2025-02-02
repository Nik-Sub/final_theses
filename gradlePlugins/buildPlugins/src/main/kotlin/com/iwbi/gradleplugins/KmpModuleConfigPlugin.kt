package com.iwbi.gradleplugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

class KmpModuleConfigPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        apply<KotlinMultiplatformPluginWrapper>()

        configureKotlin()

        val targets: KmpTargets = if (extra.has(KMP_TARGETS)) {
            extra.get(KMP_TARGETS) as KmpTargets
        } else {
            KmpTargets(
                android = findProperty("enableAndroidTarget") == "true",
                ios = findProperty("enableIosTarget") == "true",
                jvm = findProperty("enableJvmTarget") == "true",
            )
        }
        configure<KotlinMultiplatformExtension> {
            if (targets.jvm) {
                jvm()
            }

            if (targets.android) {
                plugins.apply(LibraryPlugin::class.java)
                androidTarget()
                configureAndroid()
            }

            if (targets.ios) {
                iosX64()
                iosArm64()
                iosSimulatorArm64()
            }
        }

        //apply<PublishConfigPlugin>()
    }
}

const val KMP_TARGETS = "kmpTargets"

fun Project.setKmpTargets(
    android: Boolean = false,
    ios: Boolean = false,
    jvm: Boolean = false,
) {
    extra.set(KMP_TARGETS, KmpTargets(android = android, ios = ios, jvm = jvm))
}

internal data class KmpTargets(
    val android: Boolean = false,
    val ios: Boolean = false,
    val jvm: Boolean = false,
)

/*private fun configureAndroid(project: Project) {
    project.android().apply {
        compileSdk = 34

        defaultConfig {
            minSdk = 24
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_18
            targetCompatibility = JavaVersion.VERSION_18
        }
    }
}*/

private fun Project.android(): LibraryExtension {
    return extensions.getByType(LibraryExtension::class.java)
}
