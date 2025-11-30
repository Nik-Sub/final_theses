package com.mobile.iwbi.application.friends.input

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship
import kotlinx.coroutines.flow.StateFlow

interface FriendServicePort {
    fun observeFriends(): StateFlow<List<User>>
    fun observePendingFriendRequests(): StateFlow<List<FriendRequest>>
    fun observeSentFriendRequests(): StateFlow<List<FriendRequest>>

    suspend fun searchUsers(query: String): List<User>
    suspend fun sendFriendRequest(toUserId: String): Result<FriendRequest>
    suspend fun acceptFriendRequest(requestId: String): Result<Friendship>
    suspend fun declineFriendRequest(requestId: String): Result<Unit>
    suspend fun removeFriend(friendId: String): Result<Unit>

    suspend fun getFriendsList(): List<User>
    suspend fun getPendingRequests(): List<FriendRequest>
    suspend fun getSentRequests(): List<FriendRequest>

    suspend fun refreshFriends()
    suspend fun refreshPendingFriendRequests()
    suspend fun refreshSentFriendRequests()
    suspend fun refreshAllFriendData()
}
