import org.apache.tools.ant.DirectoryScanner


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    includeBuild("gradlePlugins/buildPlugins")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "IWBI"
//include(":shared")
include(":mobile:appbase")
include(":mobile:android")
include(":mobile:infrastructure")
include(":mobile:application")
include(":mobile:domain")
include(":mobile:presentation")
include(":gradlePlugins")
include(":gradlePlugins:buildPlugins")