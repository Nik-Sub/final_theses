package com.mobile.iwbi.infrastructure.di

import com.mobile.iwbi.application.OutputPorts
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.application.helloworld.output.HelloWorldRepositoryPort
import com.mobile.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import com.mobile.iwbi.infrastructure.InfrastructureConfig
import com.mobile.iwbi.infrastructure.authentication.AuthenticationProvider
import com.mobile.iwbi.infrastructure.helloworld.HelloWorldRepository
import com.mobile.iwbi.infrastructure.shoppingnotes.ShoppingNotesRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val infrastructureModule = module {
    singleOf(::OutputPortsImpl) bind OutputPorts::class

    singleOf(::AuthenticationProvider) bind AuthenticationProviderPort::class

    single<HelloWorldRepositoryPort> {
        HelloWorldRepository(
            get(qualifier = TypeQualifier(BackendHttpClient::class)),
        )
    }

    single<ShoppingNotesRepositoryPort> {
        ShoppingNotesRepository()
    }

    single(TypeQualifier(BackendHttpClient::class)) {
        val config: InfrastructureConfig = get()

        HttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30.seconds.inWholeMilliseconds
                socketTimeoutMillis = 30.seconds.inWholeMilliseconds
            }
            defaultRequest {
                url(config.apiBaseUrl.removeSuffix("/") + "/api/")
                accept(ContentType.Application.Json)
            }
        }
    }
}

internal class OutputPortsImpl(
    override val authenticationProviderPort: AuthenticationProviderPort,
    override val helloWorldRepositoryPort: HelloWorldRepositoryPort,
    override val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort
) : OutputPorts

object BackendHttpClient