@file:Suppress("NoUnusedImports") // https://github.com/detekt/detekt/issues/7360

package com.iwbi.gradleplugins.module

import com.iwbi.gradleplugins.ktx.getLibVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

internal fun Project.configureKotlin() {
    val jvmTarget = getLibVersion("jvmTarget")

    pluginManager.apply("dev.zacsweers.redacted")

    kotlinExtension.apply {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(jvmTarget)
            vendor = JvmVendorSpec.ADOPTIUM
        }
    }

    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            apiVersion.set(KotlinVersion.KOTLIN_2_0)
            languageVersion.set(KotlinVersion.KOTLIN_2_0)

            //allWarningsAsErrors.set(runningInIde.map { !it })
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xexpect-actual-classes"
            )

            if (this is KotlinJvmCompilerOptions) {
                this.jvmTarget.set(JvmTarget.fromTarget(jvmTarget))
            }
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        targetCompatibility = jvmTarget
        sourceCompatibility = jvmTarget
    }

    /*tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            if (!runningOnCi.get()) {
                events("passed", "skipped", "failed", "standard_out", "standard_error")
            }
            exceptionFormat = TestExceptionFormat.FULL
        }
        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }
    }*/

    /*tasks.withType<KotlinJsTest>().configureEach {
        useKarma {
            useChromeHeadlessNoSandbox()
        }
    }*/
}
