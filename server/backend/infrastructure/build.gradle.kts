plugins {
    id("jvm-config")
}

dependencies {
    implementation(projects.modules.common)
    implementation(projects.server.backend.application)
    implementation(libs.gitlive.firebase.auth)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlin.logging)
}
