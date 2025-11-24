package com.server.iwbi.application.shoppingnotes.output

import com.iwbi.domain.shopping.ShoppingNote

interface ShoppingNotesRepositoryPort {
    suspend fun getShoppingNotes(): List<ShoppingNote>
    suspend fun getShoppingNotesForUser(userId: String): List<ShoppingNote>
    suspend fun getShoppingNote(noteId: String): ShoppingNote?
    suspend fun saveShoppingNote(note: ShoppingNote): ShoppingNote
    suspend fun updateShoppingNote(note: ShoppingNote)
    suspend fun deleteShoppingNote(noteId: String)
}
