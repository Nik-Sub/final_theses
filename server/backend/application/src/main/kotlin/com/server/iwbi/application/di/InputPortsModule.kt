package com.server.iwbi.application.di

import com.server.iwbi.application.InputPorts
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val inputPortsModule = module {
    singleOf(::InputPortsImpl) bind InputPorts::class
}

internal class InputPortsImpl(

) : InputPorts