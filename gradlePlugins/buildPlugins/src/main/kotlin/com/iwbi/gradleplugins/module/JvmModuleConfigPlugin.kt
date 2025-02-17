package com.iwbi.gradleplugins.module

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class JvmModuleConfigPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply<KotlinPluginWrapper>()

        configureKotlin()
    }
}
