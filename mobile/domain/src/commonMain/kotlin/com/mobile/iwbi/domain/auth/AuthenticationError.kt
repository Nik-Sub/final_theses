package com.mobile.iwbi.domain.auth

/**
 * An authentication error type
 */
sealed class AuthenticationError : Throwable() {
    /** The user was invalid */
    class InvalidUser(override val message: String?) : AuthenticationError()

    /** The provided credentials are invalid */
    class InvalidCredentials(override val message: String?) : AuthenticationError()

    /** A user with the same email already exists */
    class UserCollision(override val message: String?) : AuthenticationError()

    /** The provided password is too weak */
    class WeakPassword(override val message: String?) : AuthenticationError()

    /** User cancelled the authentication flow */
    data object CancelledByUser : AuthenticationError()

    /** Some other error occurred */
    class Other(override val message: String?, override val cause: Throwable?) : AuthenticationError()
}
