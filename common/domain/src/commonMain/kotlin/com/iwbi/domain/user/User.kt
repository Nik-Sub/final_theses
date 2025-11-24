package com.iwbi.domain.user

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
