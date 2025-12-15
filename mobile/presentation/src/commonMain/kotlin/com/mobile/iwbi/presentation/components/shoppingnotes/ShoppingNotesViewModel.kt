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
import com.mobile.iwbi.presentation.uistate.ShoppingNotesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ShoppingNotesViewModel(
    private val shoppingNotesServicePort: ShoppingNotesServicePort,
    private val authenticationServicePort: AuthenticationServicePort,
    private val friendServicePort: FriendServicePort
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingNotesUiState())
    val uiState: StateFlow<ShoppingNotesUiState> = _uiState.asStateFlow()

    private var selectedNoteId: String? = null

    init {
        observeShoppingNotes()
        loadFriends()
        observeCurrentUser()
        loadPredefinedTemplates()
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
            newItemText = "",
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

    fun addItemToSelectedNote() {
        val selectedNote = _uiState.value.selectedNote
        val itemText = _uiState.value.newItemText.trim()

        if (selectedNote != null && itemText.isNotEmpty()) {
            addItemToNote(selectedNote.id, itemText)
            _uiState.value = _uiState.value.copy(newItemText = "")
        }
    }

    fun createNewNote(title: String, sharedWith: List<String> = emptyList()) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Get current user from authentication service
                val currentUser = authenticationServicePort.observeCurrentUser().value
                println("Creating new shopping note for user: ${currentUser?.uid}")
                if (currentUser != null) {
                    // Wait for the note creation to complete
                    val createdNote = shoppingNotesServicePort.createShoppingNote(
                        title = title,
                        createdBy = currentUser.uid,
                        userIds = sharedWith
                    )
                    println("Successfully created note: ${createdNote.id}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Unable to create note: User not authenticated",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                println("Failed to create note: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to create note: ${e.message}",
                    isLoading = false
                )
            }
        }
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
            selectedFriends = emptySet()
        )
    }

    fun cancelSharing() {
        _uiState.value = _uiState.value.copy(
            selectedNoteForSharing = null,
            isSharing = false,
            selectedFriends = emptySet()
        )
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Template functionality
    private fun loadPredefinedTemplates() {
        val predefinedTemplates = listOf(
            // Grocery Essentials
            Template(
                name = "Grocery Essentials",
                items = listOf(
                    ShoppingItem(name = "Bread"),
                    ShoppingItem(name = "Milk"),
                    ShoppingItem(name = "Eggs"),
                    ShoppingItem(name = "Butter"),
                    ShoppingItem(name = "Rice"),
                    ShoppingItem(name = "Pasta"),
                    ShoppingItem(name = "Chicken"),
                    ShoppingItem(name = "Onions"),
                    ShoppingItem(name = "Tomatoes"),
                    ShoppingItem(name = "Cheese")
                )
            ),
            // Weekly Shopping
            Template(
                name = "Weekly Shopping",
                items = listOf(
                    ShoppingItem(name = "Vegetables"),
                    ShoppingItem(name = "Fruits"),
                    ShoppingItem(name = "Meat"),
                    ShoppingItem(name = "Fish"),
                    ShoppingItem(name = "Yogurt"),
                    ShoppingItem(name = "Cereal"),
                    ShoppingItem(name = "Snacks"),
                    ShoppingItem(name = "Cleaning supplies"),
                    ShoppingItem(name = "Toilet paper"),
                    ShoppingItem(name = "Shampoo")
                )
            ),
            // Party Supplies
            Template(
                name = "Party Supplies",
                items = listOf(
                    ShoppingItem(name = "Chips"),
                    ShoppingItem(name = "Drinks"),
                    ShoppingItem(name = "Cake"),
                    ShoppingItem(name = "Ice cream"),
                    ShoppingItem(name = "Balloons"),
                    ShoppingItem(name = "Plates"),
                    ShoppingItem(name = "Cups"),
                    ShoppingItem(name = "Napkins"),
                    ShoppingItem(name = "Music playlist"),
                    ShoppingItem(name = "Decorations")
                )
            ),
            // Healthy Living
            Template(
                name = "Healthy Living",
                items = listOf(
                    ShoppingItem(name = "Spinach"),
                    ShoppingItem(name = "Broccoli"),
                    ShoppingItem(name = "Quinoa"),
                    ShoppingItem(name = "Salmon"),
                    ShoppingItem(name = "Avocado"),
                    ShoppingItem(name = "Nuts"),
                    ShoppingItem(name = "Greek yogurt"),
                    ShoppingItem(name = "Berries"),
                    ShoppingItem(name = "Olive oil"),
                    ShoppingItem(name = "Green tea")
                )
            ),
            // Office Supplies
            Template(
                name = "Office Supplies",
                items = listOf(
                    ShoppingItem(name = "Pens"),
                    ShoppingItem(name = "Paper"),
                    ShoppingItem(name = "Notebooks"),
                    ShoppingItem(name = "Stapler"),
                    ShoppingItem(name = "Paper clips"),
                    ShoppingItem(name = "Highlighters"),
                    ShoppingItem(name = "Post-it notes"),
                    ShoppingItem(name = "Folders"),
                    ShoppingItem(name = "Calculator"),
                    ShoppingItem(name = "USB drive")
                )
            )
        )

        _uiState.value = _uiState.value.copy(templates = predefinedTemplates)
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

        val currentTemplates = _uiState.value.templates.toMutableList()

        // Create new template with the note's actual title
        val newTemplate = Template(
            name = note.title,
            items = note.items.map { it.copy(isChecked = false) } // Reset checked state for template
        )
        currentTemplates.add(newTemplate)

        _uiState.value = _uiState.value.copy(
            templates = currentTemplates,
            errorMessage = "\"${note.title}\" saved as template successfully!"
        )
    }

    fun removeTemplate(template: Template) {
        val currentTemplates = _uiState.value.templates.toMutableList()
        currentTemplates.remove(template)

        _uiState.value = _uiState.value.copy(
            templates = currentTemplates,
            errorMessage = "Template \"${template.name}\" removed successfully!"
        )
    }

    fun isTemplateAlreadyExists(noteTitle: String): Boolean {
        return _uiState.value.templates.any { it.name.equals(noteTitle, ignoreCase = true) }
    }

    fun canRemoveTemplate(template: Template): Boolean {
        // Allow removal of custom templates (not predefined ones)
        val predefinedTemplateNames = setOf(
            "Grocery Essentials",
            "Weekly Shopping",
            "Party Supplies",
            "Healthy Living",
            "Office Supplies"
        )
        return !predefinedTemplateNames.contains(template.name)
    }
}
