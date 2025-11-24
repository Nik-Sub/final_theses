package com.iwbi.domain.user

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}

@Serializable
data class FriendRequest(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val status: FriendRequestStatus = FriendRequestStatus.PENDING,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val respondedAt: Long? = null
)

@Serializable
data class Friendship(
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
