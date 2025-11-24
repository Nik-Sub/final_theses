package com.mobile.iwbi.application.shoppingnotes.output

import com.iwbi.domain.shopping.ShoppingNote
import kotlinx.coroutines.flow.StateFlow

interface ShoppingNotesRepositoryPort {
    fun observeShoppingNotes(): StateFlow<List<ShoppingNote>>
    fun observeShoppingNote(noteId: String): StateFlow<ShoppingNote?>
    suspend fun saveShoppingNote(note: ShoppingNote): ShoppingNote
    suspend fun updateShoppingNote(note: ShoppingNote)
    suspend fun deleteShoppingNote(noteId: String)
    suspend fun getShoppingNote(noteId: String): ShoppingNote?
}
