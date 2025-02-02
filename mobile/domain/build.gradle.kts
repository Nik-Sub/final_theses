plugins {
    id("mobile-config") version "1.0.0"
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.mobile.iwbi.domain"
}

dependencies {
    commonMainApi(libs.kotlinx.serialization.json)
    commonMainApi(projects.common.domain)
}
