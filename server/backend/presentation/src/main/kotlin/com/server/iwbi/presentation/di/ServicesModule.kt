package com.server.iwbi.presentation.di


import com.server.iwbi.application.InputPorts
import com.server.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.iwbi.util.koin.ktx.singleUsing
import com.server.iwbi.application.friends.input.FriendServicePort
import org.koin.dsl.module

internal val servicesModule = module {
    singleUsing<ShoppingNotesServicePort, InputPorts> { it.shoppingNotesServicePort }
    singleUsing<FriendServicePort, InputPorts> { it.friendServicePort }
}
