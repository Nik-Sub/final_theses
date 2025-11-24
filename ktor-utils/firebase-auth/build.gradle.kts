plugins {
    id("jvm-config")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(libs.ktor.server.auth)
    api(libs.ktor.server.authJwt)
    api(libs.ktor.server.sessions)
    api(libs.ktor.server.statusPages)
    api(libs.kotlin.logging)

    implementation(libs.firebase.admin)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.authJwt)
}

kotlin {
    explicitApi()
}
