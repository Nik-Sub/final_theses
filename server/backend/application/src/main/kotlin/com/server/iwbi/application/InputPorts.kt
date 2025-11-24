package com.server.iwbi.application

import com.server.iwbi.application.friends.input.FriendServicePort
import com.server.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort

interface InputPorts {
    val friendServicePort: FriendServicePort
    val shoppingNotesServicePort: ShoppingNotesServicePort
}