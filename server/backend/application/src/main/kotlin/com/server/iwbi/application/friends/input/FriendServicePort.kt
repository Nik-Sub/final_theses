package com.server.iwbi.application.friends.input

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship

interface FriendServicePort {
    suspend fun searchUsers(userId: String, query: String): List<User>
    suspend fun getFriends(userId: String): List<User>
    suspend fun getPendingRequests(userId: String): List<FriendRequest>
    suspend fun getSentRequests(userId: String): List<FriendRequest>

    suspend fun sendFriendRequest(userId: String, toUserId: String): FriendRequest
    suspend fun acceptFriendRequest(userId: String, requestId: String): Friendship
    suspend fun declineFriendRequest(userId: String, requestId: String)
    suspend fun removeFriend(userId: String, friendId: String)

    suspend fun getUserById(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
}
