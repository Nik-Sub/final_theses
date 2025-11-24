package com.mobile.iwbi.presentation.components.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iwbi.domain.shopping.ShoppingNote
import com.iwbi.domain.user.User
import com.mobile.iwbi.application.InputPorts
import com.mobile.iwbi.presentation.uistate.ShareNoteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ShareNoteViewModel(
    private val inputPorts: InputPorts
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareNoteUiState())
    val uiState: StateFlow<ShareNoteUiState> = _uiState.asStateFlow()

    private var currentNoteId: String? = null

    fun loadNote(noteId: String) {
        if (currentNoteId == noteId) return // Already loaded
        currentNoteId = noteId

        viewModelScope.launch {
            // Combine note data and friends list
            combine(
                inputPorts.shoppingNotesServicePort.observeShoppingNote(noteId),
                inputPorts.friendServicePort.observeFriends()
            ) { note, friends ->
                _uiState.value = _uiState.value.copy(
                    note = note,
                    availableFriends = friends.filter { friend ->
                        // Filter out friends who already have access to this note
                        note?.sharedWith?.contains(friend.id) != true
                    },
                    selectedFriends = emptySet(),
                    errorMessage = null
                )
            }
        }
    }

    fun toggleFriendSelection(friendId: String, isSelected: Boolean) {
        val currentSelection = _uiState.value.selectedFriends.toMutableSet()
        if (isSelected) {
            currentSelection.add(friendId)
        } else {
            currentSelection.remove(friendId)
        }
        _uiState.value = _uiState.value.copy(selectedFriends = currentSelection)
    }

    fun shareNoteWithSelectedFriends() {
        val noteId = currentNoteId ?: return
        val selectedFriends = _uiState.value.selectedFriends

        if (selectedFriends.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please select at least one friend to share with"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSharing = true, errorMessage = null)

            try {
                val result = inputPorts.shoppingNotesServicePort.shareNoteWithFriends(
                    noteId = noteId,
                    friendIds = selectedFriends.toList()
                )

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        shareSuccess = true,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSharing = false,
                        shareSuccess = false,
                        errorMessage = "Failed to share note: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSharing = false,
                    shareSuccess = false,
                    errorMessage = "Failed to share note: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
