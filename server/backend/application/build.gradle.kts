plugins {
    id("jvm-config")
}

dependencies{
    //api(projects.gradlePlugins.buildPlugins)
    api(projects.server.domain)

    implementation(projects.modules.common)
}
