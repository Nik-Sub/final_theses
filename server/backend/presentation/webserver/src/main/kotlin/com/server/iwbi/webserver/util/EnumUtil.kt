package com.server.iwbi.webserver.util

import kotlinx.serialization.json.Json

inline fun <reified T : Enum<T>> deserializeEnum(serialName: String): T {
    return Json.decodeFromString("\"$serialName\"")
}
