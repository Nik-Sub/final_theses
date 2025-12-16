package com.mobile.iwbi.application.di

import com.mobile.iwbi.application.InputPorts
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.friends.input.FriendServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.application.templates.input.TemplateServicePort
import com.mobile.iwbi.application.user.input.UserRegistrationServicePort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val inputPortsModule = module {
    factory<InputPorts> {
        println("🎯 Creating fresh InputPorts")
        InputPortsImpl(
            authenticationServicePort = get(),
            friendServicePort = get(),
            helloWorldServicePort = get(),
            shoppingNotesServicePort = get(),
            templateServicePort = get(),
            userRegistrationServicePort = get()
        )
    }
}

internal class InputPortsImpl(
    override val authenticationServicePort: AuthenticationServicePort,
    override val friendServicePort: FriendServicePort,
    override val helloWorldServicePort: HelloWorldServicePort,
    override val shoppingNotesServicePort: ShoppingNotesServicePort,
    override val templateServicePort: TemplateServicePort,
    override val userRegistrationServicePort: UserRegistrationServicePort
) : InputPorts
