package com.mobile.iwbi.application.helloworld

import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.application.helloworld.output.HelloWorldRepositoryPort

class HelloWorldService(
    private val helloWorldRepositoryPort: HelloWorldRepositoryPort
): HelloWorldServicePort {
    override suspend fun getHelloWorld(): String {
        return helloWorldRepositoryPort.getHelloWorld()
    }

}