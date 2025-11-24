plugins {
    id("jvm-config")
}

dependencies {
    implementation(projects.modules.common)
    implementation(projects.server.backend.application)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlin.logging)

    // Exposed Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.h2database)
    implementation(libs.mysql.connector.j)
}
