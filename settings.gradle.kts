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
include(":common")
include("common:domain")
include(":server")
include(":server:domain")
include(":server:backend:application")
include(":server:backend:infrastructure")
include(":server:backend:presentation")
include(":modules:common")
include(":server:backend:presentation:webserver")