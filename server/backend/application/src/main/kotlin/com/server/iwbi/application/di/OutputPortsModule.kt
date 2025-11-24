package com.server.iwbi.application.di

import com.iwbi.util.koin.ktx.singleUsing
import com.server.iwbi.application.OutputPorts
import com.server.iwbi.application.friends.output.FriendRepositoryPort
import com.server.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import org.koin.dsl.module

val outputPortsModule = module {
    singleUsing<FriendRepositoryPort, OutputPorts> { it.friendRepositoryPort }
    singleUsing<ShoppingNotesRepositoryPort, OutputPorts> { it.shoppingNotesRepositoryPort }
}