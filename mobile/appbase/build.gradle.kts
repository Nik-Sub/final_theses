plugins {
    id("mobile-config") version "1.0.0"
}

android {
    namespace = "com.mobile.iwbi.appbase"
}

dependencies {
    commonMainApi(projects.mobile.presentation)
}
