plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.redacted) apply false
}

// Include plugin tasks with build task
tasks.matching { it.name == "build" }.configureEach {
    dependsOn(gradle.includedBuild("buildPlugins").task(":build"))
}

tasks.register("checkFirebase") {
    doLast {
        configurations["runtimeClasspath"].incoming.resolutionResult.allDependencies.forEach {
            if (it.toString().contains("firebase")) {
                println(it)
            }
        }
    }
}

// Database development tasks
tasks.register<Exec>("startDatabase") {
    group = "database"
    description = "Start MySQL database for development"
    commandLine("docker-compose", "-f", "docker-compose.dev.yml", "up", "-d")
    doLast {
        println("MySQL database is starting. Wait for health check to pass before running the server.")
        println("You can check status with: docker-compose -f docker-compose.dev.yml ps")
        println("Or view logs with: docker-compose -f docker-compose.dev.yml logs mysql")
    }
}

tasks.register<Exec>("stopDatabase") {
    group = "database"
    description = "Stop MySQL database for development"
    commandLine("docker-compose", "-f", "docker-compose.dev.yml", "down")
    doLast {
        println("MySQL database stopped.")
    }
}

tasks.register<Exec>("resetDatabase") {
    group = "database"
    description = "Stop and remove MySQL database (including data)"
    commandLine("docker-compose", "-f", "docker-compose.dev.yml", "down", "-v")
    doLast {
        println("MySQL database stopped and data removed.")
    }
}

tasks.register<Exec>("databaseStatus") {
    group = "database"
    description = "Check status of MySQL database"
    commandLine("docker-compose", "-f", "docker-compose.dev.yml", "ps")
}

tasks.register<Exec>("databaseLogs") {
    group = "database"
    description = "Show MySQL database logs"
    commandLine("docker-compose", "-f", "docker-compose.dev.yml", "logs", "mysql")
}
