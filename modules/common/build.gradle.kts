plugins {
    id("kmp-config")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.iwbi.modules.common"
}

dependencies {
    commonMainApi(libs.kotlin.logging)
    commonMainApi(libs.kotlinx.coroutines.core)
    commonMainApi(libs.kotlinx.datetime)
    commonMainApi(libs.kotlinx.serialization.core)

    commonMainApi(libs.koin.core)
}
