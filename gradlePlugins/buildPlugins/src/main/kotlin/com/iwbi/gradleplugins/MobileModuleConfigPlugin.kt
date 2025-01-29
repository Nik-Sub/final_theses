package com.iwbi.gradleplugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class MobileModuleConfigPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        setKmpTargets(android = true, ios = true)

        apply<KmpModuleConfigPlugin>()
    }
}
