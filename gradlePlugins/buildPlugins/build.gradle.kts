import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.iwbi.gradleplugins"
version = "1.0.0"

gradlePlugin {
    plugins.create("kmp-config-plugin") {
        id = "kmp-config"
        implementationClass = "com.iwbi.gradleplugins.module.KmpModuleConfigPlugin"
    }
    plugins.create("mobile-config-plugin") {
        id = "mobile-config"
        implementationClass = "com.iwbi.gradleplugins.module.MobileModuleConfigPlugin"
    }
    plugins.create("jvm-config-plugin") {
        id = "jvm-config"
        implementationClass = "com.iwbi.gradleplugins.module.JvmModuleConfigPlugin"
    }
}

dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.androidGradlePlugin)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
}
