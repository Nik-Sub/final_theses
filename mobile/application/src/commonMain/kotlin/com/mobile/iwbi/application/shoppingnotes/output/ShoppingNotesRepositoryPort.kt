package com.mobile.iwbi.application.shoppingnotes.output
import com.iwbi.domain.shopping.ShoppingNote
import kotlinx.coroutines.flow.Flow

interface ShoppingNotesRepositoryPort {
    fun observeShoppingNotes(userId: String): Flow<List<ShoppingNote>>
    fun observeShoppingNote(noteId: String, userId: String): Flow<ShoppingNote?>
    suspend fun saveShoppingNote(note: ShoppingNote): ShoppingNote
    suspend fun updateShoppingNote(note: ShoppingNote)
    suspend fun deleteShoppingNote(noteId: String)
    suspend fun getShoppingNote(noteId: String): ShoppingNote?
}
