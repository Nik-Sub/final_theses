package com.server.iwbi.webserver.config

import kotlinx.serialization.Serializable

@Serializable
data class WebServerConfig(
    val host: String,
    val port: Int,
    val baseUrl: String,
    //val authentication: AuthenticationConfig? = null,
)
