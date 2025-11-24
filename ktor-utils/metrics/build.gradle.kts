plugins {
    id("jvm-config")
}

dependencies {
    api(libs.micrometer.core)

    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.prometheus)
}

kotlin {
    explicitApi()
}
