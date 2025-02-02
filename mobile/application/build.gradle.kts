plugins {
    id("mobile-config") version "1.0.0"
}

android {
    namespace = "com.mobile.iwbi.application"
}

dependencies{
    //api(projects.gradlePlugins.buildPlugins)
    commonMainApi(projects.mobile.domain)
}
