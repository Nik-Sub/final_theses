package com.mobile.iwbi.application.authentication.output

import com.mobile.iwbi.domain.auth.AuthenticationResult
import com.mobile.iwbi.domain.auth.User
import kotlinx.coroutines.flow.StateFlow

interface AuthenticationProviderPort {
    suspend fun getIdToken(): String?

    fun observeCurrentUser(): StateFlow<User?>

    suspend fun reload()

    suspend fun resendVerificationEmail(): Boolean

    //suspend fun signInWithGoogle(): AuthenticationResult

    suspend fun signInWithEmailPassword(email: String, password: String): AuthenticationResult

    suspend fun signUpWithEmailPassword(email: String, password: String): AuthenticationResult

    suspend fun signOut()
}
