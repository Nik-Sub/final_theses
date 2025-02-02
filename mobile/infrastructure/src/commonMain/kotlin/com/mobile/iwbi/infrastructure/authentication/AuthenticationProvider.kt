package com.mobile.iwbi.infrastructure.authentication

import com.mobile.iwbi.domain.auth.AuthenticationError
import com.mobile.iwbi.domain.auth.AuthenticationResult
import com.mobile.iwbi.domain.auth.User
//import com.cm.mobile.infrastructure.auth.GoogleAuthenticationProvider
import com.cm.mobile.infrastructure.auth.UserCancelledAuthenticationException
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult as FirebaseAuthResult
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AuthenticationProvider(
    //private val googleAuthenticationProvider: GoogleAuthenticationProvider,
) : AuthenticationProviderPort {
    private val logger = KotlinLogging.logger { }

    private val currentUserFlow: MutableStateFlow<User?> = MutableStateFlow(Firebase.auth.currentUser?.toUser())

    override suspend fun getIdToken(): String? {
        return Firebase.auth.currentUser?.getIdToken(false)
    }

    override fun observeCurrentUser(): StateFlow<User?> = currentUserFlow.asStateFlow()

    override suspend fun reload() {
        logger.info { "reload()" }
        Firebase.auth.currentUser?.reload()
        currentUserFlow.value = Firebase.auth.currentUser?.toUser()
    }

    override suspend fun resendVerificationEmail(): Boolean {
        logger.info { "resendVerificationEmail()" }
        val user = Firebase.auth.currentUser
        return if (user?.isEmailVerified == false) {
            sendVerificationEmail(user)
            true
        } else {
            false
        }
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): AuthenticationResult = runWithErrorHandling {
        logger.info { "signInWithEmailPassword()" }
        Firebase.auth.signInWithEmailAndPassword(email, password).also {
            if (!it.user!!.isEmailVerified) {
                sendVerificationEmail(it.user!!)
            }
        }
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String): AuthenticationResult = runWithErrorHandling {
        logger.info { "signUpWithEmailPassword()" }
        Firebase.auth.createUserWithEmailAndPassword(email, password).also {
            if (!it.user!!.isEmailVerified) {
                sendVerificationEmail(it.user!!)
            }
        }
    }

    private suspend fun sendVerificationEmail(user: FirebaseUser) {
        logger.info { "sendVerificationEmail($user)" }
        user.sendEmailVerification()
    }

    /*override suspend fun signInWithGoogle() = runWithErrorHandling {
        logger.info { "signInWithGoogle()" }
        googleAuthenticationProvider.signInWithGoogle()
    }*/

    override suspend fun signOut() {
        logger.info { "signOut()" }

        Firebase.auth.signOut()
        currentUserFlow.value = null
    }

    private suspend fun runWithErrorHandling(block: suspend () -> FirebaseAuthResult): AuthenticationResult = runCatching {
        val authResult = block()
        val firebaseUser = authResult.user!!
        Firebase.auth.updateCurrentUser(firebaseUser)

        val user = firebaseUser.toUser()
        currentUserFlow.value = user

        if (user.isEmailVerified) {
            AuthenticationResult.Success(user)
        } else {
            AuthenticationResult.EmailVerificationRequired(user)
        }
    }.getOrElse { e ->
        logger.error(e) { "Authentication failure: " }
        val error = when (e) {
            is FirebaseAuthInvalidCredentialsException -> AuthenticationError.InvalidCredentials(e.message)
            is FirebaseAuthInvalidUserException -> AuthenticationError.InvalidUser(e.message)
            is FirebaseAuthUserCollisionException -> AuthenticationError.UserCollision(e.message)
            is FirebaseAuthWeakPasswordException -> AuthenticationError.WeakPassword(e.message)
            is UserCancelledAuthenticationException -> AuthenticationError.CancelledByUser
            else -> AuthenticationError.Other(e.message, e.cause)
        }
        AuthenticationResult.Failure(error)
    }
}

private fun FirebaseUser.toUser() = User(
    uid = uid,
    displayName = displayName?.takeIf { it.isNotBlank() },
    email = email,
    photoUrl = photoURL,
    isEmailVerified = isEmailVerified
)
