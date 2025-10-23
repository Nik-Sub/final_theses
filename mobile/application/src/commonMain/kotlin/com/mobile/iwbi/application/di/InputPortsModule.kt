package com.mobile.iwbi.application.di

import com.mobile.iwbi.application.InputPorts
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val inputPortsModule = module {
    singleOf(::InputPortsImpl) bind InputPorts::class
}

internal class InputPortsImpl(
    override val authenticationServicePort: AuthenticationServicePort,
    override val helloWorldServicePort: HelloWorldServicePort,
    override val shoppingNotesServicePort: ShoppingNotesServicePort

) : InputPorts
