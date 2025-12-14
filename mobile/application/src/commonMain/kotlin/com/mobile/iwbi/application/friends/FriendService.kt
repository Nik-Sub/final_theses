package com.mobile.iwbi.application.friends

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship
import com.mobile.iwbi.application.friends.input.FriendServicePort
import com.mobile.iwbi.application.friends.output.FriendRepositoryPort
import kotlinx.coroutines.flow.StateFlow

class FriendService(
    private val friendRepository: FriendRepositoryPort
) : FriendServicePort {

    override fun observeFriends(): StateFlow<List<User>> {
        return friendRepository.observeFriends()
    }

    override fun observePendingFriendRequests(): StateFlow<List<FriendRequest>> {
        return friendRepository.observePendingRequests()
    }

    override fun observeSentFriendRequests(): StateFlow<List<FriendRequest>> {
        return friendRepository.observeSentRequests()
    }

    override suspend fun searchUsers(query: String): List<User> {
        return friendRepository.searchUsers(query)
    }


    override suspend fun sendFriendRequest(toUserId: String): Result<FriendRequest> {
        return try {
            val friendRequest = friendRepository.sendFriendRequest(toUserId)
            Result.success(friendRequest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptFriendRequest(requestId: String): Result<Friendship> {
        return try {
            val friendship = friendRepository.acceptFriendRequest(requestId)
            Result.success(friendship)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun declineFriendRequest(requestId: String): Result<Unit> {
        return try {
            friendRepository.declineFriendRequest(requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFriend(friendId: String): Result<Unit> {
        return try {
            friendRepository.removeFriend(friendId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFriendsList(): List<User> {
        return friendRepository.getFriends()
    }

    override suspend fun getPendingRequests(): List<FriendRequest> {
        return friendRepository.getPendingRequests()
    }

    override suspend fun getSentRequests(): List<FriendRequest> {
        return friendRepository.getSentRequests()
    }

    override suspend fun refreshFriends() {
        friendRepository.refreshFriends()
    }

    override suspend fun refreshPendingFriendRequests() {
        friendRepository.refreshPendingRequests()
    }

    override suspend fun refreshSentFriendRequests() {
        friendRepository.refreshSentRequests()
    }

    override suspend fun refreshAllFriendData() {
        friendRepository.refreshAllData()
    }
}
