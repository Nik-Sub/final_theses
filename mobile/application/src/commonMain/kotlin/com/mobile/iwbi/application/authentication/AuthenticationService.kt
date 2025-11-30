package com.mobile.iwbi.application.authentication

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.application.user.input.UserRegistrationServicePort
import com.mobile.iwbi.domain.auth.AuthenticationResult
import com.mobile.iwbi.domain.auth.User
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.StateFlow

internal class AuthenticationService(
    private val authenticationProviderPort: AuthenticationProviderPort,
    private val userRegistrationServicePort: UserRegistrationServicePort,
) : AuthenticationServicePort {

    private val logger = KotlinLogging.logger {}

    override suspend fun getIdToken(): String? = authenticationProviderPort.getIdToken()

    override fun observeCurrentUser(): StateFlow<User?> = authenticationProviderPort.observeCurrentUser()

    override suspend fun reload() = authenticationProviderPort.reload()

    override suspend fun resendVerificationEmail(): Boolean =
        authenticationProviderPort.resendVerificationEmail()

    //override suspend fun signInWithGoogle() = authenticationProviderPort.signInWithGoogle()

    override suspend fun signInWithEmailPassword(email: String, password: String): AuthenticationResult {
        val result = authenticationProviderPort.signInWithEmailPassword(email, password)
        logger.info { "NIKOLAaaaa ${result}" }
        // If sign-in is successful, attempt to register the user in the backend
        if (result is AuthenticationResult.Success) {
            result.user.let { user ->
                try {
                    logger.info { "Attempting to register user after sign-in: ${user.uid}" }
                    userRegistrationServicePort.registerUser(user)
                } catch (e: Exception) {
                    logger.warn(e) { "Failed to register user after sign-in, but continuing: ${user.uid}" }
                    // Don't fail the sign-in if user registration fails
                }
            }
        }

        return result
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String): AuthenticationResult {
        val result = authenticationProviderPort.signUpWithEmailPassword(email, password)

        // If sign-up is successful, register the user in the backend
        if (result is AuthenticationResult.Success) {
            result.user?.let { user ->
                try {
                    logger.info { "Registering user after successful sign-up: ${user.uid}" }
                    userRegistrationServicePort.registerUser(user)
                    logger.info { "User registration completed: ${user.uid}" }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to register user after sign-up: ${user.uid}" }
                    // Don't fail the sign-up if user registration fails - user is authenticated in Firebase
                }
            }
        }

        return result
    }

    override suspend fun signOut() = authenticationProviderPort.signOut()
}
