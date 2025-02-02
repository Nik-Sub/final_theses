package com.mobile.iwbi.appbase.di

import com.mobile.iwbi.di.presentationModule
import createApplicationLayer
import createInfrastructureLayer
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

class KoinIntializer {
    fun init(config: KoinAppDeclaration?) = startKoin{
        config?.invoke(this)

        val outputPorts = createInfrastructureLayer().outputPorts
        val inputPorts = createApplicationLayer(outputPorts).inputPorts

        modules(module {
            single { inputPorts }
        })

        modules(presentationModule)

    }
}