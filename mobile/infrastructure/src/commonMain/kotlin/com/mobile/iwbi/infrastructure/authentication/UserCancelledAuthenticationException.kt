package com.cm.mobile.infrastructure.auth

/**
 * Indicated that user has cancelled the authentication flow
 */
class UserCancelledAuthenticationException(cause: Throwable? = null) : Exception(cause)
