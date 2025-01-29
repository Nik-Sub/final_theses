import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//
plugins {
    `kotlin-dsl`
}

group = "com.iwbi.gradleplugins"
version = "1.0.0"

gradlePlugin {
    plugins.create("kmp-config-plugin") {
        id = "kmp-config"
        implementationClass = "com.iwbi.gradleplugins.KmpModuleConfigPlugin"
    }
    plugins.create("mobile-config-plugin") {
        id = "mobile-config"
        implementationClass = "com.iwbi.gradleplugins.MobileModuleConfigPlugin"
    }
}

dependencies {
    //implementation(libs.androidGradlePlugin)
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.androidGradlePlugin)
}

/*detekt {
    config.setFrom(files(rootDir.parentFile.resolve("../config/detekt/detekt.yml")))
}*/

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
}

/*val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "18"
}*/

/*publishing {
    publications {
        create<MavenPublication>("kmpConfigPluginMaven") {
            from(components["java"])
            artifactId = "kmp-config-plugin"
        }
        create<MavenPublication>("mobileConfigPluginMaven") {
            from(components["java"])
            artifactId = "mobile-config-plugin"
        }
    }
    repositories {
        mavenLocal() // Use mavenLocal for local testing
    }
}*/
