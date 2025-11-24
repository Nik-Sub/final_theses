package com.server.iwbi.webserver.config

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class AuthenticationConfig(
    val adminFile: String = System.getenv("FIREBASE_ADMIN_FILE") ?: error("Missing firebase admin file path"),
    val webFilePath: String,
    val sessionDirectory: String,
    val sessionEncryptSecret: String,
    val sessionSignSecret: String
)
