package com.mobile.iwbi.presentation.uistate

import com.iwbi.domain.user.User
import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.shopping.ShoppingNote

data class FriendsUiState(
    val friends: List<User> = emptyList(),
    val pendingRequests: List<FriendRequest> = emptyList(),
    val sentRequests: List<FriendRequest> = emptyList(),
    val searchResults: List<User> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ShareNoteUiState(
    val note: ShoppingNote? = null,
    val availableFriends: List<User> = emptyList(),
    val selectedFriends: Set<String> = emptySet(),
    val isSharing: Boolean = false,
    val shareSuccess: Boolean = false,
    val errorMessage: String? = null
)
