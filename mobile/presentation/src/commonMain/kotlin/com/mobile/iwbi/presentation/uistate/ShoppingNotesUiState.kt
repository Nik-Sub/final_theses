package com.mobile.iwbi.presentation.uistate

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.iwbi.domain.user.User
import com.mobile.iwbi.presentation.components.shoppingnotes.NoteCategory

data class ShoppingNotesUiState(
    val myNotes: List<ShoppingNote> = emptyList(),
    val sharedNotes: List<ShoppingNote> = emptyList(),
    val selectedCategory: NoteCategory = NoteCategory.MY_NOTES,
    val selectedNote: ShoppingNote? = null,
    val isEditingNote: Boolean = false,
    val newItemText: String = "",
    val templates: List<List<ShoppingItem>> = emptyList(),
    val friends: List<User> = emptyList(),
    val selectedNoteForSharing: ShoppingNote? = null,
    val isSharing: Boolean = false,
    val selectedFriends: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val editedNoteTitle: String = ""
)