plugins {
    id("jvm-config")
}

dependencies {
    api(libs.kotlinx.serialization.json)
    api(projects.common.domain)
}
