package com.mobile.iwbi.appbase.di

import com.mobile.iwbi.presentation.di.presentationModule
import com.mobile.iwbi.application.createApplicationLayer
import com.mobile.iwbi.infrastructure.InfrastructureConfig
import com.mobile.iwbi.infrastructure.createInfrastructureLayer
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

class KoinIntializer(
    private val infrastructureConfig: InfrastructureConfig
) {
    fun init(config: KoinAppDeclaration?) = startKoin{
        config?.invoke(this)

        val outputPorts = createInfrastructureLayer(infrastructureConfig).outputPorts
        val inputPorts = createApplicationLayer(outputPorts).inputPorts

        modules(module {
            single { inputPorts }
        })

        modules(presentationModule)

    }
}