package com.mobile.iwbi.application.friends.output

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship
import kotlinx.coroutines.flow.StateFlow

interface FriendRepositoryPort {
    fun observeFriends(): StateFlow<List<User>>
    fun observePendingRequests(): StateFlow<List<FriendRequest>>
    fun observeSentRequests(): StateFlow<List<FriendRequest>>

    suspend fun searchUsers(query: String): List<User>
    suspend fun sendFriendRequest(toUserId: String): FriendRequest
    suspend fun acceptFriendRequest(requestId: String): Friendship
    suspend fun declineFriendRequest(requestId: String)
    suspend fun removeFriend(friendId: String)
    suspend fun getFriends(): List<User>
    suspend fun getPendingRequests(): List<FriendRequest>
    suspend fun  getSentRequests(): List<FriendRequest>
}
