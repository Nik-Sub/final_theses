package com.mobile.iwbi.presentation.components.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.mobile.iwbi.application.InputPorts
import com.mobile.iwbi.presentation.uistate.FriendsUiState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val inputPorts: InputPorts
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        observeFriendData()
    }

    private fun observeFriendData() {
        viewModelScope.launch {
            combine(
                inputPorts.friendServicePort.observeFriends(),
                inputPorts.friendServicePort.observePendingFriendRequests(),
                inputPorts.friendServicePort.observeSentFriendRequests()
            ) { friends, pendingRequests, sentRequests ->
                _uiState.value = _uiState.value.copy(
                    friends = friends,
                    pendingRequests = pendingRequests,
                    sentRequests = sentRequests,
                    isLoading = false
                )
            }
        }
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                searchResults = emptyList()
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isLoading = true
            )

            try {
                val results = inputPorts.friendServicePort.searchUsers(query)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to search users: ${e.message}"
                )
            }
        }
    }

    fun sendFriendRequest(userId: String) {
        KotlinLogging.logger("NIK").info { "sendFriendRequest to userId: $userId" }
        viewModelScope.launch {
            try {
                val result = inputPorts.friendServicePort.sendFriendRequest(userId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = null
                    )
                    // Remove from search results after sending request
                    _uiState.value = _uiState.value.copy(
                        searchResults = _uiState.value.searchResults.filter { it.id != userId }
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to send friend request: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to send friend request: ${e.message}"
                )
            }
        }
    }

    fun acceptFriendRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val result = inputPorts.friendServicePort.acceptFriendRequest(requestId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(errorMessage = null)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to accept request: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to accept request: ${e.message}"
                )
            }
        }
    }

    fun declineFriendRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val result = inputPorts.friendServicePort.declineFriendRequest(requestId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(errorMessage = null)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to decline request: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to decline request: ${e.message}"
                )
            }
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            try {
                val result = inputPorts.friendServicePort.removeFriend(friendId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(errorMessage = null)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to remove friend: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to remove friend: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList()
        )
    }
}
