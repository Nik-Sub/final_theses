package com.mobile.iwbi.application

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.friends.input.FriendServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort

interface InputPorts {
    val authenticationServicePort: AuthenticationServicePort
    val friendServicePort: FriendServicePort
    val helloWorldServicePort: HelloWorldServicePort
    val shoppingNotesServicePort: ShoppingNotesServicePort
}