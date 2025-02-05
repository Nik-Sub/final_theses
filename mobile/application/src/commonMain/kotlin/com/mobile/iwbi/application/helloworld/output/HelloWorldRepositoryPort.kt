package com.mobile.iwbi.application.helloworld.output

interface HelloWorldRepositoryPort {
    suspend fun getHelloWorld(): String
}