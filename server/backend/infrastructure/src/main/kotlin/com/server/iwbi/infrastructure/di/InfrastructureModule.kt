package com.server.iwbi.infrastructure.di

import com.server.iwbi.application.OutputPorts
import com.server.iwbi.application.friends.output.FriendRepositoryPort
import com.server.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val infrastructureModule = module {
    singleOf(::OutputPortsImpl) bind OutputPorts::class
}

internal class OutputPortsImpl(
    override val friendRepositoryPort: FriendRepositoryPort,
    override val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort
) : OutputPorts