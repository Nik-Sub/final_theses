plugins {
    id("kmp-config") version "1.0.0"
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.iwbi.utils.common"
}

dependencies {
    commonMainApi(libs.kotlin.logging)
    commonMainApi(libs.kotlinx.coroutines.core)
    commonMainApi(libs.kotlinx.datetime)
    commonMainApi(libs.kotlinx.serialization.core)
    commonMainApi(libs.slf4j.api)

    commonMainApi(libs.koin.core)
}
