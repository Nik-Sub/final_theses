plugins {
    id("jvm-config")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(projects.modules.common)

    implementation(projects.server.backend.application)
    api(projects.server.backend.presentation.webserver)
}
