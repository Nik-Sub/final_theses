package com.mobile.iwbi.infrastructure.di

import com.mobile.iwbi.application.OutputPorts
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.application.friends.output.FriendRepositoryPort
import com.mobile.iwbi.application.helloworld.output.HelloWorldRepositoryPort
import com.mobile.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import com.mobile.iwbi.application.user.output.UserRepositoryPort
import com.mobile.iwbi.infrastructure.InfrastructureConfig
import com.mobile.iwbi.infrastructure.authentication.AuthenticationProvider
import com.mobile.iwbi.infrastructure.friends.FriendRepository
import com.mobile.iwbi.infrastructure.helloworld.HelloWorldRepository
import com.mobile.iwbi.infrastructure.shoppingnotes.ShoppingNotesRepository
import com.mobile.iwbi.infrastructure.user.UserApi
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
    factory<OutputPorts> {
        println("🏭 Creating fresh OutputPorts")
        OutputPortsImpl(
            authenticationProviderPort = get(),
            friendRepositoryPort = get(),
            helloWorldRepositoryPort = get(),
            shoppingNotesRepositoryPort = get(),
            userRepositoryPort = get()
        )
    }

    singleOf(::AuthenticationProvider) bind AuthenticationProviderPort::class

    factory<HelloWorldRepositoryPort> {
        println("🏭 Creating fresh HelloWorldRepository")
        HelloWorldRepository(
            httpClientProvider = { get(qualifier = TypeQualifier(BackendHttpClient::class)) }
        )
    }

    factory<FriendRepositoryPort> {
        println("🏭 Creating fresh FriendRepository")
        FriendRepository(
            httpClientProvider = { get(qualifier = TypeQualifier(BackendHttpClient::class)) },
            authProvider = get()
        )
    }

    factory<ShoppingNotesRepositoryPort> {
        println("🏭 Creating fresh ShoppingNotesRepository")
        ShoppingNotesRepository(
            httpClientProvider = { get(qualifier = TypeQualifier(BackendHttpClient::class)) },
            authProvider = get()
        )
    }

    factory<UserRepositoryPort> {
        println("🏭 Creating fresh UserApi")
        UserApi(
            httpClientProvider = { get(qualifier = TypeQualifier(BackendHttpClient::class)) }
        )
    }

    // HttpClient as factory - gets fresh instance each time with current auth token
    factory(TypeQualifier(BackendHttpClient::class)) {
        val config: InfrastructureConfig = get()
        val authProvider: AuthenticationProviderPort = get()

        println("🔄 Mobile: Creating fresh HttpClient instance")
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = authProvider.getIdToken()
                        println("🔐 Mobile: Loading token: ${token?.take(20)}...")
                        token?.let { BearerTokens(it, "") }
                    }
                    refreshTokens {
                        val token = authProvider.getIdToken(forceRefresh = true)
                        println("🔄 Mobile: Refreshing token: ${token?.take(20)}...")
                        token?.let { BearerTokens(it, "") }
                    }
                    sendWithoutRequest { true }
                }
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
    override val friendRepositoryPort: FriendRepositoryPort,
    override val helloWorldRepositoryPort: HelloWorldRepositoryPort,
    override val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort,
    override val userRepositoryPort: UserRepositoryPort
) : OutputPorts

object BackendHttpClient
