plugins {
    id("jvm-config")
}

dependencies {
    implementation(projects.modules.common)
    implementation(projects.server.backend.application)
    implementation(projects.server.backend.infrastructure.persistence)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlin.logging)
}
