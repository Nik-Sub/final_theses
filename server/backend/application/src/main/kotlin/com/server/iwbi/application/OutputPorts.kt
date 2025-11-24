package com.server.iwbi.application

import com.server.iwbi.application.friends.output.FriendRepositoryPort
import com.server.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort

interface OutputPorts {
    val friendRepositoryPort: FriendRepositoryPort
    val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort
}