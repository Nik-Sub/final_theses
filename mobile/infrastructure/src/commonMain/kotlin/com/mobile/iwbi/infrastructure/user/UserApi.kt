package com.mobile.iwbi.infrastructure.user

import com.mobile.iwbi.application.user.output.UserRepositoryPort
import com.mobile.iwbi.domain.auth.User
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class UserApi(
    private val httpClientProvider: () -> HttpClient
) : UserRepositoryPort {

    private val logger = KotlinLogging.logger {}

    override suspend fun registerUser(user: User) {
        logger.info { "Calling user registration API for user: ${user.uid}" }

        httpClientProvider().post("users/register") {
            contentType(ContentType.Application.Json)
            // No body needed - user data comes from Firebase token
        }

        logger.info { "User registration API call completed for user: ${user.uid}" }
    }
}
