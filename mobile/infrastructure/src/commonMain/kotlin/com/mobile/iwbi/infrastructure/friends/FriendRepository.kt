package com.mobile.iwbi.infrastructure.friends

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.Friendship
import com.mobile.iwbi.application.friends.output.FriendRepositoryPort
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class FriendRepository(
    private val httpClient: HttpClient,
) : FriendRepositoryPort {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _friends = MutableStateFlow<List<User>>(emptyList())
    private val _pendingRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    private val _sentRequests = MutableStateFlow<List<FriendRequest>>(emptyList())

    init {
        println("DEBUG: FriendRepository - Initializing...")
        scope.launch {
            println("DEBUG: FriendRepository - Starting initial data refresh...")
            refreshAllData()
            println("DEBUG: FriendRepository - Initial data refresh completed")
        }
    }

    override fun observeFriends(): StateFlow<List<User>> = _friends.asStateFlow()

    override fun observePendingRequests(): StateFlow<List<FriendRequest>> = _pendingRequests.asStateFlow()

    override fun observeSentRequests(): StateFlow<List<FriendRequest>> = _sentRequests.asStateFlow()

    override suspend fun searchUsers(query: String): List<User> {
        return httpClient.get("friends/search") {
            parameter("q", query)
        }.body<List<User>>()
    }

    override suspend fun sendFriendRequest(toUserId: String): FriendRequest {
        println("DEBUG: Sending friend request to user: $toUserId")
        val request = SendFriendRequestRequest(toUserId)
        val friendRequest = httpClient.post("friends/requests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<FriendRequest>()

        println("DEBUG: Friend request sent successfully: $friendRequest")
        // Refresh sent requests to update UI
        refreshSentRequests()
        return friendRequest
    }

    override suspend fun acceptFriendRequest(requestId: String): Friendship {
        val friendship = httpClient.post("friends/requests/$requestId/accept").body<Friendship>()

        // Refresh all relevant data since accepting creates friendship and removes from pending
        refreshFriends()
        refreshPendingRequests()
        refreshSentRequests() // In case current user sent a request that was accepted
        return friendship
    }

    override suspend fun declineFriendRequest(requestId: String) {
        httpClient.post("friends/requests/$requestId/decline")

        // Refresh both pending and sent requests
        refreshPendingRequests()
        refreshSentRequests() // In case current user sent a request that was declined
    }

    override suspend fun removeFriend(friendId: String) {
        httpClient.delete("friends/$friendId")

        // Refresh friends list
        refreshFriends()
    }

    override suspend fun getFriends(): List<User> {
        return httpClient.get("friends").body<List<User>>()
    }

    override suspend fun getPendingRequests(): List<FriendRequest> {
        return httpClient.get("friends/requests/pending").body<List<FriendRequest>>()
    }

    override suspend fun getSentRequests(): List<FriendRequest> {
        println("DEBUG: getSentRequests() called - Making HTTP request to friends/requests/sent")
        try {
            val result = httpClient.get("friends/requests/sent").body<List<FriendRequest>>()
            println("DEBUG: getSentRequests() - HTTP request successful, received ${result.size} requests: $result")
            return result
        } catch (e: Exception) {
            println("ERROR: getSentRequests() - HTTP request failed: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun refreshAllData() {
        try {
            refreshFriends()
            refreshPendingRequests()
            refreshSentRequests()
        } catch (e: Exception) {
            println("Error refreshing friend data: ${e.message}")
        }
    }

    override suspend fun refreshFriends() {
        try {
            val friends = getFriends()
            _friends.value = friends
        } catch (e: Exception) {
            println("Error refreshing friends: ${e.message}")
        }
    }

    override suspend fun refreshPendingRequests() {
        try {
            val requests = getPendingRequests()
            _pendingRequests.value = requests
        } catch (e: Exception) {
            println("Error refreshing pending requests: ${e.message}")
        }
    }

    override suspend fun refreshSentRequests() {
        try {
            println("DEBUG: Refreshing sent requests...")
            val requests = getSentRequests()
            println("DEBUG: Setting StateFlow with ${requests.size} sent requests")
            val oldValue = _sentRequests.value
            _sentRequests.value = requests
            println("DEBUG: StateFlow updated - old: ${oldValue.size}, new: ${_sentRequests.value.size}")
            println("DEBUG: StateFlow now contains: ${_sentRequests.value}")
        } catch (e: Exception) {
            println("Error refreshing sent requests: ${e.message}")
            e.printStackTrace()
        }
    }
}

@Serializable
data class SendFriendRequestRequest(
    val toUserId: String
)
