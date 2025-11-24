package com.server.iwbi.persistence.friends

import com.benasher44.uuid.uuid4
import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship
import com.iwbi.domain.user.FriendRequestStatus
import com.server.iwbi.application.friends.output.FriendRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.Clock

class FriendRepository : FriendRepositoryPort {

    private val users = createMockUsers().toMutableList()
    private val friendRequests = mutableListOf<FriendRequest>()
    private val friendships = createMockFriendships().toMutableList()

    override suspend fun searchUsers(query: String, excludeUserId: String): List<User> {
        val us = users.filter { user ->
            user.id != excludeUserId &&
            (user.displayName.contains(query, ignoreCase = true) ||
             user.email.contains(query, ignoreCase = true))
        }

        KotlinLogging.logger("NIKOLA").info { "searchUsers with query='$query' found ${us.size} users" }

        return us
    }

    override suspend fun getFriends(userId: String): List<User> {
        val friendIds = friendships
            .filter { it.user1Id == userId || it.user2Id == userId }
            .map { if (it.user1Id == userId) it.user2Id else it.user1Id }

        return users.filter { it.id in friendIds }
    }

    override suspend fun getPendingRequests(userId: String): List<FriendRequest> {
        return friendRequests.filter {
            it.toUserId == userId && it.status == FriendRequestStatus.PENDING
        }
    }

    override suspend fun getSentRequests(userId: String): List<FriendRequest> {
        return friendRequests.filter {
            it.fromUserId == userId && it.status == FriendRequestStatus.PENDING
        }
    }

    override suspend fun createFriendRequest(fromUserId: String, toUserId: String): FriendRequest {
        val request = FriendRequest(
            id = uuid4().toString(),
            fromUserId = fromUserId,
            toUserId = toUserId,
            status = FriendRequestStatus.PENDING,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        friendRequests.add(request)
        return request
    }

    override suspend fun acceptFriendRequest(requestId: String, userId: String): Friendship {
        val request = friendRequests.find {
            it.id == requestId && it.toUserId == userId && it.status == FriendRequestStatus.PENDING
        } ?: throw IllegalArgumentException("Friend request not found or not authorized")

        // Update request status
        val updatedRequest = request.copy(
            status = FriendRequestStatus.ACCEPTED,
            respondedAt = Clock.System.now().toEpochMilliseconds()
        )
        friendRequests.removeAll { it.id == requestId }
        friendRequests.add(updatedRequest)

        // Create friendship
        val friendship = Friendship(
            id = uuid4().toString(),
            user1Id = request.fromUserId,
            user2Id = request.toUserId,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        friendships.add(friendship)
        return friendship
    }

    override suspend fun declineFriendRequest(requestId: String, userId: String) {
        val request = friendRequests.find {
            it.id == requestId && it.toUserId == userId && it.status == FriendRequestStatus.PENDING
        } ?: throw IllegalArgumentException("Friend request not found or not authorized")

        val updatedRequest = request.copy(
            status = FriendRequestStatus.DECLINED,
            respondedAt = Clock.System.now().toEpochMilliseconds()
        )
        friendRequests.removeAll { it.id == requestId }
        friendRequests.add(updatedRequest)
    }

    override suspend fun removeFriend(userId: String, friendId: String) {
        friendships.removeAll {
            (it.user1Id == userId && it.user2Id == friendId) ||
            (it.user1Id == friendId && it.user2Id == userId)
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return users.find { it.id == userId }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun createUser(user: User): User {
        users.add(user)
        return user
    }

    override suspend fun updateUser(user: User): User {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
        }
        return user
    }

    override suspend fun areFriends(userId1: String, userId2: String): Boolean {
        return friendships.any {
            (it.user1Id == userId1 && it.user2Id == userId2) ||
            (it.user1Id == userId2 && it.user2Id == userId1)
        }
    }

    override suspend fun hasPendingRequest(fromUserId: String, toUserId: String): Boolean {
        return friendRequests.any {
            it.fromUserId == fromUserId && it.toUserId == toUserId && it.status == FriendRequestStatus.PENDING
        }
    }

    private fun createMockUsers(): List<User> {
        return listOf(
            User(
                id = "user1",
                email = "john@example.com",
                displayName = "John Doe",
                profilePictureUrl = null
            ),
            User(
                id = "user2",
                email = "jane@example.com",
                displayName = "Jane Smith",
                profilePictureUrl = null
            ),
            User(
                id = "user3",
                email = "bob@example.com",
                displayName = "Bob Johnson",
                profilePictureUrl = null
            ),
            User(
                id = "user4",
                email = "alice@example.com",
                displayName = "Alice Brown",
                profilePictureUrl = null
            ),
            User(
                id = "user5",
                email = "charlie@example.com",
                displayName = "Charlie Wilson",
                profilePictureUrl = null
            )
        )
    }

    private fun createMockFriendships(): List<Friendship> {
        return listOf(
            Friendship(
                id = "friendship1",
                user1Id = "user1",
                user2Id = "user2"
            ),
            Friendship(
                id = "friendship2",
                user1Id = "user1",
                user2Id = "user3"
            )
        )
    }
}
