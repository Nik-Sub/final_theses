package com.server.iwbi.application.shoppingnotes.input

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote

interface ShoppingNotesServicePort {
    suspend fun getShoppingNotes(userId: String): List<ShoppingNote>
    suspend fun getShoppingNote(noteId: String, userId: String): ShoppingNote?
    suspend fun createShoppingNote(title: String, createdBy: String, userIds: List<String>): ShoppingNote
    suspend fun updateShoppingNote(note: ShoppingNote, userId: String)
    suspend fun toggleItem(noteId: String, itemIndex: Int, userId: String)
    suspend fun addItem(noteId: String, item: ShoppingItem, userId: String)
    suspend fun removeItem(noteId: String, itemIndex: Int, userId: String)
    suspend fun shareNoteWithUser(noteId: String, newUserId: String, userId: String)
    suspend fun deleteShoppingNote(noteId: String, userId: String)
}
