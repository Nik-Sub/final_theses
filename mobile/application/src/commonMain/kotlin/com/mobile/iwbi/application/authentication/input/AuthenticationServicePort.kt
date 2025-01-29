/*
package com.mobile.iwbi.application.authentication.input

interface AuthenticationServicePort {
    */
/**
     * Returns the id token of the current user, or null if no user is logged-in
     *//*

    suspend fun getIdToken(): String?

    */
/**
     * A state-flow that provides the current user. Is null when no user is logged-in
     *//*

    fun observeCurrentUser(): StateFlow<User?>

    */
/**
     * Reloads the state of the current-user. Can be used to update whether the user's email has been verified
     *//*

    suspend fun reload()

    */
/**
     * Resends the verification email for the current-user
     * @return true if the email was sent, false otherwise (e.g. if no user is logged-in or their email is already verified)
     *//*

    suspend fun resendVerificationEmail(): Boolean

    */
/**
     * Triggers the sign-in flow using google. Returns an [AuthenticationResult]
     *//*

    suspend fun signInWithGoogle(): AuthenticationResult

    */
/**
     * Triggers sign-in with the given email and password. Returns an [AuthenticationResult]
     *//*

    suspend fun signInWithEmailPassword(email: String, password: String): AuthenticationResult

    */
/**
     * Triggers sign-up with the given email and password. Returns an [AuthenticationResult]
     *//*

    suspend fun signUpWithEmailPassword(email: String, password: String): AuthenticationResult

    */
/**
     * Signs out the current user
     *//*

    suspend fun signOut()
}
*/
