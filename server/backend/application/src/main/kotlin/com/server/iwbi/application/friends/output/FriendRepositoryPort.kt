package com.server.iwbi.application.friends.output

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship

interface FriendRepositoryPort {
    suspend fun searchUsers(query: String, excludeUserId: String): List<User>
    suspend fun getFriends(userId: String): List<User>
    suspend fun getPendingRequests(userId: String): List<FriendRequest>
    suspend fun getSentRequests(userId: String): List<FriendRequest>

    suspend fun createFriendRequest(fromUserId: String, toUserId: String): FriendRequest
    suspend fun acceptFriendRequest(requestId: String, userId: String): Friendship
    suspend fun declineFriendRequest(requestId: String, userId: String)
    suspend fun removeFriend(userId: String, friendId: String)

    suspend fun getUserById(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User): User
    suspend fun updateUser(user: User): User

    suspend fun areFriends(userId1: String, userId2: String): Boolean
    suspend fun hasPendingRequest(fromUserId: String, toUserId: String): Boolean
}
