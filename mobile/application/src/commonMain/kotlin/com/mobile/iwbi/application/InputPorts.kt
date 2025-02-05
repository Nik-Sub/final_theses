package com.mobile.iwbi.application

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort

interface InputPorts {
    val authenticationServicePort: AuthenticationServicePort
    val helloWorldServicePort: HelloWorldServicePort
}