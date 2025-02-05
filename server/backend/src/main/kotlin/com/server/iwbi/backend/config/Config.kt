package com.server.iwbi.backend.config

import com.server.iwbi.webserver.config.WebServerConfig
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val webServer: WebServerConfig,
    //val database: DatabaseConfig = DatabaseConfig(),
)
