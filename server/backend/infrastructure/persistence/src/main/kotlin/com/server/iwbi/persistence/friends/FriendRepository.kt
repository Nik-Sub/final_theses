package com.server.iwbi.persistence.friends

import com.benasher44.uuid.uuid4
import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship
import com.server.iwbi.application.friends.output.FriendRepositoryPort
import com.server.iwbi.persistence.database.DatabaseProvider
import com.server.iwbi.persistence.friends.daos.*
import com.server.iwbi.persistence.friends.tables.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class FriendRepository(
    databaseProvider: DatabaseProvider
) : FriendRepositoryPort {

    private val database by databaseProvider

    override suspend fun searchUsers(query: String, excludeUserId: String): List<User> {
        return transaction(database) {
            val users = UserDAO.find {
                (UsersTable.userId neq excludeUserId) and
                ((UsersTable.displayName like "%$query%") or (UsersTable.email like "%$query%"))
            }.map { it.toDomain() }

            KotlinLogging.logger("NIKOLA").info { "searchUsers with query='$query' found ${users.size} users" }
            users
        }
    }

    override suspend fun getFriends(userId: String): List<User> {
        return transaction(database) {
            val friendIds = FriendshipDAO.find {
                (FriendshipsTable.user1Id eq userId) or (FriendshipsTable.user2Id eq userId)
            }.map { friendship ->
                if (friendship.user1Id == userId) friendship.user2Id else friendship.user1Id
            }

            UserDAO.find { UsersTable.userId inList friendIds }.map { it.toDomain() }
        }
    }

    override suspend fun getPendingRequests(userId: String): List<FriendRequest> {
        return transaction(database) {
            FriendRequestDAO.find {
                (FriendRequestsTable.toUserId eq userId) and (FriendRequestsTable.status eq "PENDING")
            }.map { it.toDomain() }
        }
    }

    override suspend fun getSentRequests(userId: String): List<FriendRequest> {
        return transaction(database) {
            FriendRequestDAO.find {
                (FriendRequestsTable.fromUserId eq userId) and (FriendRequestsTable.status eq "PENDING")
            }.map { it.toDomain() }
        }
    }

    override suspend fun createFriendRequest(fromUserId: String, toUserId: String): FriendRequest {
        return transaction(database) {
            val requestId = uuid4().toString()
            val currentTime = Clock.System.now().toEpochMilliseconds()

            val requestDAO = FriendRequestDAO.new {
                this.requestId = requestId
                this.fromUserId = fromUserId
                this.toUserId = toUserId
                this.status = "PENDING"
                this.createdAt = currentTime
                this.respondedAt = null
            }

            requestDAO.toDomain()
        }
    }

    override suspend fun acceptFriendRequest(requestId: String, userId: String): Friendship {
        return transaction(database) {
            val request = FriendRequestDAO.find {
                (FriendRequestsTable.requestId eq requestId) and
                (FriendRequestsTable.toUserId eq userId) and
                (FriendRequestsTable.status eq "PENDING")
            }.firstOrNull() ?: throw IllegalArgumentException("Friend request not found or not authorized")

            val currentTime = Clock.System.now().toEpochMilliseconds()

            // Update request status
            request.status = "ACCEPTED"
            request.respondedAt = currentTime

            // Create friendship
            val friendshipId = uuid4().toString()
            val friendshipDAO = FriendshipDAO.new {
                this.friendshipId = friendshipId
                this.user1Id = request.fromUserId
                this.user2Id = request.toUserId
                this.createdAt = currentTime
            }

            friendshipDAO.toDomain()
        }
    }

    override suspend fun declineFriendRequest(requestId: String, userId: String) {
        transaction(database) {
            val request = FriendRequestDAO.find {
                (FriendRequestsTable.requestId eq requestId) and
                (FriendRequestsTable.toUserId eq userId) and
                (FriendRequestsTable.status eq "PENDING")
            }.firstOrNull() ?: throw IllegalArgumentException("Friend request not found or not authorized")

            request.status = "DECLINED"
            request.respondedAt = Clock.System.now().toEpochMilliseconds()
        }
    }

    override suspend fun removeFriend(userId: String, friendId: String) {
        transaction(database) {
            FriendshipDAO.find {
                ((FriendshipsTable.user1Id eq userId) and (FriendshipsTable.user2Id eq friendId)) or
                ((FriendshipsTable.user1Id eq friendId) and (FriendshipsTable.user2Id eq userId))
            }.forEach { it.delete() }
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return transaction(database) {
            UserDAO.find { UsersTable.userId eq userId }.firstOrNull()?.toDomain()
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return transaction(database) {
            UserDAO.find { UsersTable.email eq email }.firstOrNull()?.toDomain()
        }
    }

    override suspend fun createUser(user: User): User {
        return transaction(database) {
            val userDAO = UserDAO.new {
                this.userId = user.id
                this.email = user.email
                this.displayName = user.displayName
                this.profilePictureUrl = user.profilePictureUrl
                this.createdAt = user.createdAt
            }
            userDAO.toDomain()
        }
    }

    override suspend fun updateUser(user: User): User {
        return transaction(database) {
            val userDAO = UserDAO.find { UsersTable.userId eq user.id }.firstOrNull()
                ?: throw IllegalArgumentException("User not found: ${user.id}")

            userDAO.email = user.email
            userDAO.displayName = user.displayName
            userDAO.profilePictureUrl = user.profilePictureUrl

            userDAO.toDomain()
        }
    }

    override suspend fun areFriends(userId1: String, userId2: String): Boolean {
        return transaction(database) {
            FriendshipDAO.find {
                ((FriendshipsTable.user1Id eq userId1) and (FriendshipsTable.user2Id eq userId2)) or
                ((FriendshipsTable.user1Id eq userId2) and (FriendshipsTable.user2Id eq userId1))
            }.any()
        }
    }

    override suspend fun hasPendingRequest(fromUserId: String, toUserId: String): Boolean {
        return transaction(database) {
            FriendRequestDAO.find {
                (FriendRequestsTable.fromUserId eq fromUserId) and
                (FriendRequestsTable.toUserId eq toUserId) and
                (FriendRequestsTable.status eq "PENDING")
            }.any()
        }
    }
}
