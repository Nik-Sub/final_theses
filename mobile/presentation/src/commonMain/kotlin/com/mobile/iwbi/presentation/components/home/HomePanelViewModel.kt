package com.mobile.iwbi.presentation.components.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iwbi.domain.shopping.ShoppingItem
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.presentation.uistate.HomePanelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomePanelViewModel(
    private val shoppingNotesServicePort: ShoppingNotesServicePort,
    private val authenticationServicePort: AuthenticationServicePort
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomePanelUiState())
    val uiState: StateFlow<HomePanelUiState> = _uiState.asStateFlow()

    init {
        loadShoppingNotes()
    }

    private fun loadShoppingNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            shoppingNotesServicePort.observeShoppingNotes()
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
        _uiState.value = _uiState.value.copy(
            selectedNote = note,
            isEditingNote = note != null
        )
    }

    fun exitEditMode() {
        _uiState.value = _uiState.value.copy(
            selectedNote = null,
            isEditingNote = false,
            newItemText = ""
        )
    }

    fun updateNewItemText(text: String) {
        _uiState.value = _uiState.value.copy(newItemText = text)
    }

    fun addItemToSelectedNote() {
        val selectedNote = _uiState.value.selectedNote
        val itemText = _uiState.value.newItemText.trim()

        if (selectedNote != null && itemText.isNotEmpty()) {
            addItemToNote(selectedNote.id, itemText)
            _uiState.value = _uiState.value.copy(newItemText = "")
        }
    }

    fun createNewNote(title: String, sharedWith: List<String>) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.createShoppingNote(
                    title = title,
                    createdBy = authenticationServicePort.observeCurrentUser().value?.uid ?: "",
                    userIds = sharedWith
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun toggleItem(noteId: String, itemIndex: Int) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.toggleItem(
                    noteId = noteId,
                    itemIndex = itemIndex,
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
                    createdBy = authenticationServicePort.observeCurrentUser().value?.uid ?: "",
                    userIds = emptyList()
                )

                // Add all template items to the new note
                template.forEach { item ->
                    shoppingNotesServicePort.addItem(
                        noteId = note.id,
                        item = item,
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}