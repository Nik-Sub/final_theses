package com.mobile.iwbi.application.shoppingnotes.input

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import kotlinx.coroutines.flow.StateFlow

interface ShoppingNotesServicePort {
    fun observeShoppingNotes(): StateFlow<List<ShoppingNote>>
    fun observeShoppingNote(noteId: String): StateFlow<ShoppingNote?>
    suspend fun createShoppingNote(title: String, createdBy: String, userIds: List<String>): ShoppingNote
    suspend fun updateShoppingNote(note: ShoppingNote)
    suspend fun toggleItem(noteId: String, itemIndex: Int)
    suspend fun addItem(noteId: String, item: ShoppingItem)
    suspend fun removeItem(noteId: String, itemIndex: Int)
    suspend fun shareNoteWithUser(noteId: String, newUserId: String)
    suspend fun deleteShoppingNote(noteId: String)

    // Friend-based sharing methods
    suspend fun shareNoteWithFriends(noteId: String, friendIds: List<String>): Result<Unit>
    suspend fun removeFriendFromNote(noteId: String, friendId: String): Result<Unit>
    fun observeShareableNotes(): StateFlow<List<ShoppingNote>>
    fun observeNotesSharedWithMe(): StateFlow<List<ShoppingNote>>
}