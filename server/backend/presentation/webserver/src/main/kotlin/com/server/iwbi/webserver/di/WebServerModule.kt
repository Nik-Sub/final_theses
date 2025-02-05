package com.server.iwbi.webserver.di

import com.server.iwbi.webserver.WebServer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val webServerModule = module {
    singleOf(::WebServer)
}
