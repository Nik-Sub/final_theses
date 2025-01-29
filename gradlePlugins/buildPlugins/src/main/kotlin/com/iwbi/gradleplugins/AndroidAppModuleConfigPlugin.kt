package com.iwbi.gradleplugins

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

class AndroidAppModuleConfigPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply<AppPlugin>()
        apply<KotlinAndroidPluginWrapper>()
        /*apply<DetektConfigPlugin>()
        apply<KtlintConfigPlugin>()
        apply<PublishConfigPlugin>()*/

        configureAndroid()
        configureKotlin()
    }
}
