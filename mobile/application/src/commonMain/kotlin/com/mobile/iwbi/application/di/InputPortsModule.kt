package com.mobile.iwbi.application.di

import InputPorts
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val inputPortsModule = module {
    singleOf(::InputPortsImpl) bind InputPorts::class
}

internal class InputPortsImpl(
    override val authenticationServicePort: AuthenticationServicePort
) : InputPorts