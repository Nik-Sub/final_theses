package com.server.iwbi.infrastructure.di

import com.server.iwbi.application.OutputPorts
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val infrastructureModule = module {
    singleOf(::OutputPortsImpl) bind OutputPorts::class
}

internal class OutputPortsImpl(
) : OutputPorts