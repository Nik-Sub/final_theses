package com.server.iwbi.webserver.util

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip

fun Application.configureCompression() {
    install(Compression) {
        gzip()
    }
}
