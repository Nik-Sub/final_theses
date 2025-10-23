package com.mobile.iwbi.infrastructure.shoppingnotes

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.mobile.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ShoppingNotesRepository : ShoppingNotesRepositoryPort {

    private val _shoppingNotes = MutableStateFlow(createMockData())

    override fun observeShoppingNotes(userId: String): Flow<List<ShoppingNote>> {
        return _shoppingNotes.map { notes ->
            notes.filter { note ->
                note.createdBy == userId || note.sharedWith.contains(userId)
            }
        }
    }

    override fun observeShoppingNote(noteId: String, userId: String): Flow<ShoppingNote?> {
        return _shoppingNotes.map { notes ->
            notes.find { note ->
                note.id == noteId && (note.createdBy == userId || note.sharedWith.contains(userId))
            }
        }
    }

    override suspend fun saveShoppingNote(note: ShoppingNote): ShoppingNote {
        val currentNotes = _shoppingNotes.value.toMutableList()
        currentNotes.add(note)
        _shoppingNotes.value = currentNotes
        return note
    }

    override suspend fun updateShoppingNote(note: ShoppingNote) {
        val currentNotes = _shoppingNotes.value.toMutableList()
        val index = currentNotes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            currentNotes[index] = note
            _shoppingNotes.value = currentNotes
        }
    }

    override suspend fun deleteShoppingNote(noteId: String) {
        val currentNotes = _shoppingNotes.value.toMutableList()
        currentNotes.removeAll { it.id == noteId }
        _shoppingNotes.value = currentNotes
    }

    override suspend fun getShoppingNote(noteId: String): ShoppingNote? {
        return _shoppingNotes.value.find { it.id == noteId }
    }

    private fun createMockData(): List<ShoppingNote> {
        return listOf(
            ShoppingNote(
                id = "note1",
                title = "Grocery Shopping",
                items = listOf(
                    ShoppingItem("Milk"),
                    ShoppingItem("Bread"),
                    ShoppingItem("Eggs", true)
                ),
                createdBy = "user1",
                sharedWith = listOf("user2")
            ),
            ShoppingNote(
                id = "note2",
                title = "Weekend BBQ",
                items = listOf(
                    ShoppingItem("Steaks"),
                    ShoppingItem("Charcoal"),
                    ShoppingItem("Beer", true),
                    ShoppingItem("Vegetables")
                ),
                createdBy = "user1",
                sharedWith = listOf("user2", "user3")
            ),
            ShoppingNote(
                id = "note3",
                title = "Party Supplies",
                items = listOf(
                    ShoppingItem("Balloons"),
                    ShoppingItem("Cake"),
                    ShoppingItem("Drinks"),
                    ShoppingItem("Plates")
                ),
                createdBy = "user2",
                sharedWith = listOf("user1")
            )
        )
    }
}
