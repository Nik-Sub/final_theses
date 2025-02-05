package com.server.iwbi.backend.di

import com.server.iwbi.backend.config.Config
import org.koin.core.module.Module
import org.koin.dsl.module

val configModule = module {
    singleSubConfig { it.webServer }
    //singleSubConfig { it.database }
}

private inline fun <reified T> Module.singleSubConfig(crossinline block: (Config) -> T) {
    single {
        val config: Config = get()
        block(config)
    }
}
