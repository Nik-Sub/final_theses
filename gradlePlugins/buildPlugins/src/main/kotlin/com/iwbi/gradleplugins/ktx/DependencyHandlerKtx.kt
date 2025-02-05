package com.iwbi.gradleplugins.ktx

import org.gradle.api.artifacts.dsl.DependencyHandler

@Suppress("UnusedReceiverParameter")
fun DependencyHandler.kotlinWrapper(module: String, version: String? = null): Any =
    "org.jetbrains.kotlin-wrappers:kotlin-$module${version?.let { ":$version" } ?: ""}"
