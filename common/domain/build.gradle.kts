plugins {
    id("kmp-config") version "1.0.0"
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.iwbi.utils.common"
}

dependencies {
    commonMainApi(projects.common)
}
