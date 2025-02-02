package com.mobile.iwbi.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
)
