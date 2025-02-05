package com.server.iwbi.backend.di

import com.server.iwbi.backend.App
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    includes(configModule)

    singleOf(::App)
}
