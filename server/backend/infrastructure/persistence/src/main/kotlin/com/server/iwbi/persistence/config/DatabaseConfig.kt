package com.server.iwbi.persistence.config

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfig(
    val url: String = System.getenv("DB_URL") ?: error("DB_URL not set"),
    val driver: String = System.getenv("DB_DRIVER") ?: "com.mysql.cj.jdbc.Driver",
    val user: String = System.getenv("DB_USER") ?: error("DB_USER not set"),
    val password: String = System.getenv("DB_PASSWORD") ?: error("DB_PASSWORD not set"),
)
