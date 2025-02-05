plugins {
    id("jvm-config")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(projects.modules.common)

    implementation(projects.server.backend.application)
    implementation(projects.server.backend.infrastructure)
    implementation(projects.server.backend.presentation)
    implementation(libs.ktor.server.core)
    implementation(libs.log4j.slf4j2.impl)
}
