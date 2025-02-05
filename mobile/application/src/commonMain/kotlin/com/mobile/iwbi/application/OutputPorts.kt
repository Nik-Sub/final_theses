package com.mobile.iwbi.application

import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.application.helloworld.output.HelloWorldRepositoryPort

interface OutputPorts {
    val authenticationProviderPort: AuthenticationProviderPort
    val helloWorldRepositoryPort: HelloWorldRepositoryPort
}