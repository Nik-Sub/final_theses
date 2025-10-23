package com.mobile.iwbi.application

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort

interface InputPorts {
    val authenticationServicePort: AuthenticationServicePort
    val helloWorldServicePort: HelloWorldServicePort
    val shoppingNotesServicePort: ShoppingNotesServicePort
}