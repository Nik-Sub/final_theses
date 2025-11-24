package com.mobile.iwbi.application

import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.application.friends.output.FriendRepositoryPort
import com.mobile.iwbi.application.helloworld.output.HelloWorldRepositoryPort
import com.mobile.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort

interface OutputPorts {
    val authenticationProviderPort: AuthenticationProviderPort
    val friendRepositoryPort: FriendRepositoryPort
    val helloWorldRepositoryPort: HelloWorldRepositoryPort
    val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort
}