plugins {
    id("jvm-config")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(projects.common)
    api(projects.cm.server.backend.application)
    implementation(projects.cm.common.domain)
    implementation(projects.cm.server.backend.infrastructure.grpc)
    implementation(projects.cm.server.backend.infrastructure.search)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.azure.storage.blob)
    implementation(libs.azure.data.tables)

    implementation(projects.exposedUtils)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.json)
    implementation(libs.exposed.kotlin.datetime)

    implementation(libs.mysql.connector.java)
    implementation(libs.mssql.jdbc)
    implementation(libs.postgresql.jdbc)
    implementation(libs.openai.client)
    implementation(libs.ktor.client.cio)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)

    testImplementation(projects.test.common)
    testImplementation(libs.testcontainers.mysql)
}
