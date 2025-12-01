package com.mobile.iwbi.infrastructure.helloworld

import com.mobile.iwbi.application.helloworld.output.HelloWorldRepositoryPort
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class HelloWorldRepository(
    private val httpClientProvider: () -> HttpClient
): HelloWorldRepositoryPort {
    override suspend fun getHelloWorld(): String {
        return httpClientProvider().get("hello-world").body()
    }
}