package com.server.iwbi.application.friends

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship
import com.server.iwbi.application.friends.input.FriendServicePort
import com.server.iwbi.application.friends.output.FriendRepositoryPort

class FriendService(
    private val friendRepository: FriendRepositoryPort
) : FriendServicePort {

    override suspend fun searchUsers(userId: String, query: String): List<User> {
        return friendRepository.searchUsers(query, userId)
    }

    override suspend fun getFriends(userId: String): List<User> {
        return friendRepository.getFriends(userId)
    }

    override suspend fun getPendingRequests(userId: String): List<FriendRequest> {
        return friendRepository.getPendingRequests(userId)
    }

    override suspend fun getSentRequests(userId: String): List<FriendRequest> {
        return friendRepository.getSentRequests(userId)
    }

    override suspend fun sendFriendRequest(userId: String, toUserId: String): FriendRequest {
        // Validate that users aren't already friends
        if (friendRepository.areFriends(userId, toUserId)) {
            throw IllegalStateException("Users are already friends")
        }

        // Validate that there's no pending request
        if (friendRepository.hasPendingRequest(userId, toUserId) ||
            friendRepository.hasPendingRequest(toUserId, userId)) {
            throw IllegalStateException("Friend request already exists")
        }

        return friendRepository.createFriendRequest(userId, toUserId)
    }

    override suspend fun acceptFriendRequest(userId: String, requestId: String): Friendship {
        return friendRepository.acceptFriendRequest(requestId, userId)
    }

    override suspend fun declineFriendRequest(userId: String, requestId: String) {
        friendRepository.declineFriendRequest(requestId, userId)
    }

    override suspend fun removeFriend(userId: String, friendId: String) {
        friendRepository.removeFriend(userId, friendId)
    }

    override suspend fun getUserById(userId: String): User? {
        return friendRepository.getUserById(userId)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return friendRepository.getUserByEmail(email)
    }

    override suspend fun createUser(user: User): User {
        return friendRepository.createUser(user)
    }
}
