package com.mobile.iwbi.application.helloworld.input

interface HelloWorldServicePort {
    suspend fun getHelloWorld(): String
}