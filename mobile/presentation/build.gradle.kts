plugins {
    id("mobile-config") version "1.0.0"
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.mobile.iwbi.presentation"
}

dependencies{
    //implementation(libs.compose.material3)
    //implementation(libs.compose.ui)
    commonMainApi(libs.koin.core)
    commonMainApi(projects.mobile.application)

    commonMainImplementation(compose.components.resources)
    commonMainImplementation(compose.runtime)
    commonMainImplementation(compose.foundation)
    commonMainImplementation(compose.material3)
    commonMainImplementation(compose.ui)
}
