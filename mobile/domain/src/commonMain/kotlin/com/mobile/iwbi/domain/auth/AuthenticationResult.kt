package com.mobile.iwbi.domain.auth

/**
 * The result of an authentication attempt
 */
sealed class AuthenticationResult {
    /**
     * Indicates authentication was successful. Contains the logged in user
     */
    data class Success(val user: User) : AuthenticationResult()

    /**
     * Indicates authentication was successful, but the users email still needs to be verified
     */
    data class EmailVerificationRequired(val user: User) : AuthenticationResult()

    /**
     * Indicates authentication has failed. Contains an error type
     */
    data class Failure(val error: AuthenticationError) : AuthenticationResult()
}
