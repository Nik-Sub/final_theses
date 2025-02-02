plugins {
    id("mobile-config") version "1.0.0"
}

android {
    namespace = "com.mobile.iwbi.infrastructure"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "MobileShared"
            isStatic = true
        }
    }

    sourceSets {
        iosMain
    }
}

dependencies {
    commonMainImplementation(projects.mobile.application)
    commonMainImplementation(libs.gitlive.firebase.auth)
    commonMainApi(libs.kotlinx.coroutines.core)
    commonMainApi(libs.kotlin.logging)

    androidMainImplementation(libs.google.android.playServicesAuth)
    androidMainImplementation(libs.androidx.credentials)
    androidMainImplementation(libs.androidx.credentials.playServicesAuth)
    androidMainImplementation(libs.google.android.identity.googleid)


}
