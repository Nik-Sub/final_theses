package com.mobile.iwbi.application.authentication

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.domain.auth.User
import kotlinx.coroutines.flow.StateFlow

internal class AuthenticationService(
    private val authenticationProviderPort: AuthenticationProviderPort,
) : AuthenticationServicePort {
    override suspend fun getIdToken(): String? = authenticationProviderPort.getIdToken()

    override fun observeCurrentUser(): StateFlow<User?> = authenticationProviderPort.observeCurrentUser()

    override suspend fun reload() = authenticationProviderPort.reload()

    override suspend fun resendVerificationEmail(): Boolean =
        authenticationProviderPort.resendVerificationEmail()

    //override suspend fun signInWithGoogle() = authenticationProviderPort.signInWithGoogle()

    override suspend fun signInWithEmailPassword(email: String, password: String) =
        authenticationProviderPort.signInWithEmailPassword(email, password)

    override suspend fun signUpWithEmailPassword(email: String, password: String) =
        authenticationProviderPort.signUpWithEmailPassword(email, password)

    override suspend fun signOut() = authenticationProviderPort.signOut()
}
