package com.server.iwbi.application

import com.iwbi.util.LifecycleComponent
import com.iwbi.util.koin.BaseKoinComponent
import com.server.iwbi.application.di.inputPortsModule
import com.server.iwbi.application.di.outputPortsModule
import org.koin.core.component.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module

interface AppLayer: LifecycleComponent {
    val inputPorts: InputPorts
}

fun createApplicationLayer(
    outputPorts: OutputPorts
) : AppLayer = object : AppLayer, BaseKoinComponent() {
    override val application = koinApplication {
        modules(module {
            single { outputPorts }
        })

        modules(inputPortsModule)
        modules(outputPortsModule)
    }
    override val inputPorts by inject<InputPorts>()

    override fun initialize() {}

    override fun release() {}
}