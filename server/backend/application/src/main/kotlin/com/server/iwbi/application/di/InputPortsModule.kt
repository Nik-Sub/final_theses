package com.server.iwbi.application.di

import com.server.iwbi.application.InputPorts
import com.server.iwbi.application.friends.FriendService
import com.server.iwbi.application.friends.input.FriendServicePort
import com.server.iwbi.application.shoppingnotes.ShoppingNotesService
import com.server.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val inputPortsModule = module {
    singleOf(::FriendService) bind FriendServicePort::class
    singleOf(::ShoppingNotesService) bind ShoppingNotesServicePort::class
    singleOf(::InputPortsImpl) bind InputPorts::class
}

internal class InputPortsImpl(
    override val friendServicePort: FriendServicePort,
    override val shoppingNotesServicePort: ShoppingNotesServicePort
) : InputPorts