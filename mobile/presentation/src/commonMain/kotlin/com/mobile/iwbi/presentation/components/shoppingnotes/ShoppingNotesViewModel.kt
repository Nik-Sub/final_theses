package com.mobile.iwbi.presentation.components.shoppingnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.iwbi.domain.shopping.Template
import com.iwbi.domain.user.User
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.friends.input.FriendServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.application.templates.input.TemplateServicePort
import com.mobile.iwbi.presentation.uistate.ShoppingNotesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ShoppingNotesViewModel(
    private val shoppingNotesServicePort: ShoppingNotesServicePort,
    private val authenticationServicePort: AuthenticationServicePort,
    private val friendServicePort: FriendServicePort,
    private val templateServicePort: TemplateServicePort
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingNotesUiState())
    val uiState: StateFlow<ShoppingNotesUiState> = _uiState.asStateFlow()

    private var selectedNoteId: String? = null

    init {
        observeShoppingNotes()
        loadFriends()
        observeCurrentUser()
        observeTemplates()
    }

    private fun observeShoppingNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            combine(
                shoppingNotesServicePort.observeShareableNotes(),
                shoppingNotesServicePort.observeNotesSharedWithMe()
            ) { myNotes, sharedNotes ->
                _uiState.value = _uiState.value.copy(
                    myNotes = myNotes,
                    sharedNotes = sharedNotes,
                    isLoading = false
                )
            }.collect { }
        }
    }

    private fun loadFriends() {
        viewModelScope.launch {
            friendServicePort.observeFriends().collect { friends ->
                _uiState.value = _uiState.value.copy(friends = friends)
            }
        }
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authenticationServicePort.observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(currentUserId = user?.uid)
            }
        }
    }

    private fun observeTemplates() {
        viewModelScope.launch {
            templateServicePort.observeTemplates().collect { templates ->
                _uiState.value = _uiState.value.copy(templates = templates)
            }
        }
    }

    fun selectCategory(category: NoteCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun selectNote(noteId: String) {
        selectedNoteId = noteId
        val myNote = _uiState.value.myNotes.find { it.id == noteId }
        val sharedNote = _uiState.value.sharedNotes.find { it.id == noteId }
        val note = myNote ?: sharedNote

        _uiState.value = _uiState.value.copy(
            selectedNote = note,
            isEditingNote = note != null
        )

        // Start observing the selected note for real-time updates
        if (note != null) {
            observeSelectedNote(noteId)
        }
    }

    private fun observeSelectedNote(noteId: String) {
        viewModelScope.launch {
            shoppingNotesServicePort.observeShoppingNote(noteId).collect { updatedNote ->
                if (updatedNote != null && _uiState.value.isEditingNote && _uiState.value.selectedNote?.id == noteId) {
                    _uiState.value = _uiState.value.copy(selectedNote = updatedNote)
                }
            }
        }
    }

    fun exitEditMode() {
        _uiState.value = _uiState.value.copy(
            selectedNote = null,
            isEditingNote = false,
            isCreatingNewNote = false,
            newItemText = "",
            editedNoteTitle = "",
            isSharing = false,
            selectedFriends = emptySet()
        )
    }

    fun updateNewItemText(text: String) {
        _uiState.value = _uiState.value.copy(newItemText = text)
    }

    fun updateNoteTitleText(title: String) {
        _uiState.value = _uiState.value.copy(editedNoteTitle = title)
    }

    fun saveNoteTitle() {
        val selectedNote = _uiState.value.selectedNote
        val newTitle = _uiState.value.editedNoteTitle.trim()

        if (selectedNote != null && newTitle.isNotEmpty() && newTitle != selectedNote.title) {
            if (_uiState.value.isCreatingNewNote) {
                // For draft notes, update locally
                val updatedNote = selectedNote.copy(title = newTitle)
                _uiState.value = _uiState.value.copy(
                    selectedNote = updatedNote,
                    editedNoteTitle = ""
                )
            } else {
                // For existing notes, save to backend
                viewModelScope.launch {
                    try {
                        val updatedNote = selectedNote.copy(title = newTitle)
                        shoppingNotesServicePort.updateShoppingNote(updatedNote)
                        _uiState.value = _uiState.value.copy(
                            selectedNote = updatedNote,
                            editedNoteTitle = ""
                        )
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Failed to update note title: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    fun addItemToSelectedNote() {
        val selectedNote = _uiState.value.selectedNote
        val itemText = _uiState.value.newItemText.trim()

        if (selectedNote != null && itemText.isNotEmpty()) {
            if (_uiState.value.isCreatingNewNote) {
                // For draft notes, add items locally
                val newItem = ShoppingItem(name = itemText)
                val updatedNote = selectedNote.copy(items = selectedNote.items + newItem)
                _uiState.value = _uiState.value.copy(
                    selectedNote = updatedNote,
                    newItemText = ""
                )
            } else {
                // For existing notes, save to backend
                addItemToNote(selectedNote.id, itemText)
                _uiState.value = _uiState.value.copy(newItemText = "")
            }
        }
    }

    fun createNewNote(title: String, sharedWith: List<String> = emptyList()) {
        // Create a draft note that will be saved when user clicks save
        val draftNote = ShoppingNote(
            id = "", // Empty ID indicates unsaved note
            title = title,
            items = emptyList(),
            createdBy = _uiState.value.currentUserId ?: "",
            sharedWith = sharedWith
        )

        _uiState.value = _uiState.value.copy(
            selectedNote = draftNote,
            isEditingNote = true,
            isCreatingNewNote = true,
            editedNoteTitle = title
        )
    }

    fun saveNewNote() {
        val draftNote = _uiState.value.selectedNote

        if (draftNote != null && _uiState.value.isCreatingNewNote) {
            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(isLoading = true)

                    val currentUser = authenticationServicePort.observeCurrentUser().value
                    if (currentUser != null) {
                        // Create the note with the current title
                        val createdNote = shoppingNotesServicePort.createShoppingNote(
                            title = draftNote.title,
                            createdBy = currentUser.uid,
                            userIds = draftNote.sharedWith
                        )

                        // Add all items that were added during creation
                        draftNote.items.forEach { item ->
                            shoppingNotesServicePort.addItem(createdNote.id, item)
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isCreatingNewNote = false,
                            isEditingNote = false,
                            selectedNote = null,
                            newItemText = "",
                            editedNoteTitle = ""
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to create note: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun discardNewNote() {
        _uiState.value = _uiState.value.copy(
            selectedNote = null,
            isEditingNote = false,
            isCreatingNewNote = false,
            newItemText = "",
            editedNoteTitle = ""
        )
    }

    fun createNoteFromTemplate(template: Template) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val currentUser = authenticationServicePort.observeCurrentUser().value
                if (currentUser != null) {
                    val createdNote = shoppingNotesServicePort.createShoppingNote(
                        title = template.name,
                        createdBy = currentUser.uid,
                        userIds = emptyList()
                    )

                    // Add template items to the newly created note (reset checked state)
                    template.items.forEach { item ->
                        shoppingNotesServicePort.addItem(createdNote.id, item.copy(isChecked = false))
                    }

                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to create note from template: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun toggleItem(noteId: String, itemIndex: Int) {
        if (_uiState.value.isCreatingNewNote) {
            // For draft notes, toggle items locally
            val selectedNote = _uiState.value.selectedNote
            if (selectedNote != null && itemIndex >= 0 && itemIndex < selectedNote.items.size) {
                val updatedItems = selectedNote.items.toMutableList().apply {
                    this[itemIndex] = this[itemIndex].copy(isChecked = !this[itemIndex].isChecked)
                }
                val updatedNote = selectedNote.copy(items = updatedItems)
                _uiState.value = _uiState.value.copy(selectedNote = updatedNote)
            }
        } else {
            // For existing notes, toggle on backend
            viewModelScope.launch {
                try {
                    shoppingNotesServicePort.toggleItem(noteId, itemIndex)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to toggle item: ${e.message}"
                    )
                }
            }
        }
    }

    fun addItemToNote(noteId: String, itemName: String) {
        viewModelScope.launch {
            try {
                val newItem = ShoppingItem(name = itemName)
                shoppingNotesServicePort.addItem(noteId, newItem)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add item: ${e.message}"
                )
            }
        }
    }

    fun removeItemFromNote(noteId: String, itemIndex: Int) {
        if (_uiState.value.isCreatingNewNote) {
            // For draft notes, remove items locally
            val selectedNote = _uiState.value.selectedNote
            if (selectedNote != null && itemIndex >= 0 && itemIndex < selectedNote.items.size) {
                val updatedItems = selectedNote.items.toMutableList().apply {
                    removeAt(itemIndex)
                }
                val updatedNote = selectedNote.copy(items = updatedItems)
                _uiState.value = _uiState.value.copy(selectedNote = updatedNote)
            }
        } else {
            // For existing notes, remove from backend
            viewModelScope.launch {
                try {
                    shoppingNotesServicePort.removeItem(noteId, itemIndex)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to remove item: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                shoppingNotesServicePort.deleteShoppingNote(noteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete note: ${e.message}"
                )
            }
        }
    }

    // Friend sharing functionality
    fun startSharing(noteId: String) {
        val note = _uiState.value.myNotes.find { it.id == noteId }
        _uiState.value = _uiState.value.copy(
            selectedNoteForSharing = note,
            isSharing = true,
            selectedFriends = emptySet(),
            friendSearchQuery = ""
        )
    }

    fun cancelSharing() {
        _uiState.value = _uiState.value.copy(
            selectedNoteForSharing = null,
            isSharing = false,
            selectedFriends = emptySet(),
            friendSearchQuery = ""
        )
    }

    fun updateFriendSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(friendSearchQuery = query)
    }

    fun toggleFriendSelection(friend: User) {
        val currentSelection = _uiState.value.selectedFriends
        val newSelection = if (currentSelection.contains(friend.id)) {
            currentSelection - friend.id
        } else {
            currentSelection + friend.id
        }
        _uiState.value = _uiState.value.copy(selectedFriends = newSelection)
    }

    fun shareWithSelectedFriends() {
        val noteId = _uiState.value.selectedNoteForSharing?.id
        val friendIds = _uiState.value.selectedFriends.toList()

        if (noteId != null && friendIds.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    val result = shoppingNotesServicePort.shareNoteWithFriends(noteId, friendIds)
                    if (result.isSuccess) {
                        _uiState.value = _uiState.value.copy(
                            isSharing = false,
                            selectedNoteForSharing = null,
                            selectedFriends = emptySet(),
                            errorMessage = "Note shared successfully!"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Failed to share note: ${result.exceptionOrNull()?.message}"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to share note: ${e.message}"
                    )
                }
            }
        }
    }

    fun removeFriendFromNote(friend: User) {
        val noteId = _uiState.value.selectedNoteForSharing?.id
        if (noteId != null) {
            viewModelScope.launch {
                try {
                    val result = shoppingNotesServicePort.removeFriendFromNote(noteId, friend.id)
                    if (result.isSuccess) {
                        // Update the selected note for sharing to reflect the change
                        val updatedNote = _uiState.value.selectedNoteForSharing?.copy(
                            sharedWith = _uiState.value.selectedNoteForSharing!!.sharedWith.filter { it != friend.id }
                        )
                        _uiState.value = _uiState.value.copy(
                            selectedNoteForSharing = updatedNote
                        )
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
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun saveNoteAsTemplate(noteId: String) {
        val note = _uiState.value.myNotes.find { it.id == noteId }
            ?: _uiState.value.sharedNotes.find { it.id == noteId }
            ?: return

        if (note.items.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Cannot save empty note as template"
            )
            return
        }

        // Check if template already exists
        if (isTemplateAlreadyExists(note.title)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Template \"${note.title}\" already exists!"
            )
            return
        }

        viewModelScope.launch {
            try {
                // Create new template with the note's actual title
                val newTemplate = Template(
                    name = note.title,
                    items = note.items.map { it.copy(isChecked = false) } // Reset checked state for template
                )

                templateServicePort.saveTemplate(newTemplate)

                _uiState.value = _uiState.value.copy(
                    errorMessage = "\"${note.title}\" saved as template successfully!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to save template: ${e.message}"
                )
            }
        }
    }

    fun removeTemplate(template: Template) {
        viewModelScope.launch {
            try {
                templateServicePort.deleteTemplate(template.name)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Template \"${template.name}\" removed successfully!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to remove template: ${e.message}"
                )
            }
        }
    }

    fun isTemplateAlreadyExists(noteTitle: String): Boolean {
        return _uiState.value.templates.any { it.name.equals(noteTitle, ignoreCase = true) }
    }
}
