package com.mobile.iwbi.infrastructure

import com.iwbi.util.koin.BaseKoinComponent
import com.iwbi.util.koin.ktx.declare
import com.mobile.iwbi.application.OutputPorts
import com.mobile.iwbi.infrastructure.di.infrastructureModule
import com.mobile.iwbi.infrastructure.di.platformInfrastructureModule
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

interface InfraLayer {
    val outputPorts: OutputPorts
}

fun createInfrastructureLayer(
    infrastructureConfig: InfrastructureConfig,
    platformModule: Module,
) : InfraLayer = object : InfraLayer, BaseKoinComponent() {
    override val application = koinApplication {
        declare(infrastructureConfig)
        modules(platformModule)

        modules(infrastructureModule)
    }
    override val outputPorts by inject<OutputPorts>()
}

