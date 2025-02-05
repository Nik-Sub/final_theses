package com.server.iwbi.infrastructure.di

import com.iwbi.util.LifecycleComponent
import com.iwbi.util.koin.BaseKoinComponent
import com.server.iwbi.application.OutputPorts
import org.koin.core.component.inject
import org.koin.dsl.koinApplication

interface InfraLayer: LifecycleComponent {
    val outputPorts: OutputPorts
}

fun createInfrastructureLayer() : InfraLayer = object : InfraLayer, BaseKoinComponent() {
    override val application = koinApplication {
        modules(infrastructureModule)
    }
    override val outputPorts by inject<OutputPorts>()

    override fun initialize() {}

    override fun release() {}
}

