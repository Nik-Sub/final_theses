package com.mobile.iwbi.presentation.components.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iwbi.domain.shopping.ShoppingItem
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.presentation.HomePanelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomePanelViewModel(
    private val shoppingNotesServicePort: ShoppingNotesServicePort
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomePanelUiState())
    val uiState: StateFlow<HomePanelUiState> = _uiState.asStateFlow()

    init {
        loadShoppingNotes()
    }

    private fun loadShoppingNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            shoppingNotesServicePort.observeShoppingNotes(_uiState.value.currentUserId)
                .catch {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .collect { notes ->
                    _uiState.value = _uiState.value.copy(
                        shoppingNotes = notes,
                        isLoading = false
                    )
                }
        }
    }

    fun selectNote(noteId: String) {
        val note = _uiState.value.shoppingNotes.find { it.id == noteId }
        _uiState.value = _uiState.value.copy(selectedNote = note)
    }

    fun createNewNote(title: String, shareWithUserIds: List<String> = emptyList()) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.createShoppingNote(
                    title = title,
                    createdBy = _uiState.value.currentUserId,
                    userIds = shareWithUserIds
                )
            } catch (e: Exception) {
                // Handle error - could add error state to UI
            }
        }
    }

    fun toggleItem(noteId: String, itemIndex: Int) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.toggleItem(
                    noteId = noteId,
                    itemIndex = itemIndex,
                    userId = _uiState.value.currentUserId
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addItemToNote(noteId: String, itemName: String) {
        viewModelScope.launch {
            try {
                val newItem = ShoppingItem(name = itemName)
                shoppingNotesServicePort.addItem(
                    noteId = noteId,
                    item = newItem,
                    userId = _uiState.value.currentUserId
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun removeItemFromNote(noteId: String, itemIndex: Int) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.removeItem(
                    noteId = noteId,
                    itemIndex = itemIndex,
                    userId = _uiState.value.currentUserId
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun shareNoteWithUser(noteId: String, userId: String) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.shareNoteWithUser(
                    noteId = noteId,
                    newUserId = userId,
                    userId = _uiState.value.currentUserId
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.deleteShoppingNote(
                    noteId = noteId,
                    userId = _uiState.value.currentUserId
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addTemplate(template: List<ShoppingItem>) {
        _uiState.value = _uiState.value.copy(
            templates = _uiState.value.templates + listOf(template)
        )
    }

    fun createNoteFromTemplate(template: List<ShoppingItem>, title: String) {
        viewModelScope.launch {
            try {
                val note = shoppingNotesServicePort.createShoppingNote(
                    title = title,
                    createdBy = _uiState.value.currentUserId,
                    userIds = emptyList()
                )

                // Add all template items to the new note
                template.forEach { item ->
                    shoppingNotesServicePort.addItem(
                        noteId = note.id,
                        item = item,
                        userId = _uiState.value.currentUserId
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}