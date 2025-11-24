import com.iwbi.gradleplugins.ktx.asProperties


plugins {
    id("jvm-config")
    alias(libs.plugins.kotlinx.serialization)
    application
}

application {
    mainClass.set("com.server.iwbi.Main")
}

val localProperties = project.rootProject.file("local.properties").asProperties() ?: error("local.properties file missing")

tasks.run.configure {
    args(file("config.json").takeIf { it.exists() } ?: file("server_config.json"))

    environment("FIREBASE_ADMIN_FILE", file("config/IWBIAdminConfig.json").absolutePath)

    environment("DB_URL", localProperties.getProperty("DB_URL") ?: error("DB_URL not set"))
    environment("DB_USER", localProperties.getProperty("DB_USER") ?: error("DB_USER not set"))
    environment("DB_PASSWORD", localProperties.getProperty("DB_PASSWORD") ?: error("DB_PASSWORD not set"))


    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    environment("LOG_LEVEL", "info")
}


dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.server.backend)
}
