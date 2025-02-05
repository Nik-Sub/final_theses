plugins {
    id("jvm-config")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(projects.modules.common)
    api(projects.server.backend.application)

    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cachingHeaders)
    implementation(libs.ktor.server.callLogging)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.statusPages)
}
