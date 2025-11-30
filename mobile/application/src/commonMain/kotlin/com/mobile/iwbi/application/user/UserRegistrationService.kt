package com.mobile.iwbi.application.user

import com.mobile.iwbi.application.user.input.UserRegistrationServicePort
import com.mobile.iwbi.application.user.output.UserRepositoryPort
import com.mobile.iwbi.domain.auth.User
import io.github.oshai.kotlinlogging.KotlinLogging

internal class UserRegistrationService(
    private val userRepositoryPort: UserRepositoryPort
) : UserRegistrationServicePort {

    private val logger = KotlinLogging.logger {}

    override suspend fun registerUser(user: User): Result<Unit> {
        return try {
            logger.info { "Registering user: ${user.uid}" }
            val result = userRepositoryPort.registerUser(user)
            logger.info { "User registration successful: ${user.uid}" }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error(e) { "Failed to register user: ${user.uid}" }
            Result.failure(e)
        }
    }
}
