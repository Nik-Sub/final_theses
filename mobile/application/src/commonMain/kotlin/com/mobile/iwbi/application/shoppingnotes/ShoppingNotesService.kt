package com.mobile.iwbi.application.shoppingnotes

import com.benasher44.uuid.uuid4
import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class ShoppingNotesService(
    private val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort
) : ShoppingNotesServicePort {

    override fun observeShoppingNotes(userId: String): Flow<List<ShoppingNote>> {
        return shoppingNotesRepositoryPort.observeShoppingNotes(userId)
    }

    override fun observeShoppingNote(noteId: String, userId: String): Flow<ShoppingNote?> {
        return shoppingNotesRepositoryPort.observeShoppingNote(noteId, userId)
    }

    override suspend fun createShoppingNote(title: String, createdBy: String, userIds: List<String>): ShoppingNote {
        val newNote = ShoppingNote(
            id = uuid4().toString(),
            title = title,
            items = emptyList(),
            createdBy = createdBy,
            sharedWith = userIds,
            lastModified = Clock.System.now().toEpochMilliseconds()
        )
        return shoppingNotesRepositoryPort.saveShoppingNote(newNote)
    }

    override suspend fun updateShoppingNote(note: ShoppingNote, userId: String) {
        val updatedNote = note.copy(lastModified = Clock.System.now().toEpochMilliseconds())
        shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
    }

    override suspend fun toggleItem(noteId: String, itemIndex: Int, userId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        val updatedItems = note.items.toMutableList()
        if (itemIndex < updatedItems.size) {
            updatedItems[itemIndex] = updatedItems[itemIndex].copy(
                isChecked = !updatedItems[itemIndex].isChecked
            )
            val updatedNote = note.copy(
                items = updatedItems,
                lastModified = Clock.System.now().toEpochMilliseconds()
            )
            shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
        }
    }

    override suspend fun addItem(noteId: String, item: ShoppingItem, userId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        val updatedNote = note.copy(
            items = note.items + item,
            lastModified = Clock.System.now().toEpochMilliseconds()
        )
        shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
    }

    override suspend fun removeItem(noteId: String, itemIndex: Int, userId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        if (itemIndex < note.items.size) {
            val updatedItems = note.items.toMutableList()
            updatedItems.removeAt(itemIndex)
            val updatedNote = note.copy(
                items = updatedItems,
                lastModified = Clock.System.now().toEpochMilliseconds()
            )
            shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
        }
    }

    override suspend fun shareNoteWithUser(noteId: String, newUserId: String, userId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        if (!note.sharedWith.contains(newUserId)) {
            val updatedNote = note.copy(
                sharedWith = note.sharedWith + newUserId,
                lastModified = Clock.System.now().toEpochMilliseconds()
            )
            shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
        }
    }

    override suspend fun deleteShoppingNote(noteId: String, userId: String) {
        shoppingNotesRepositoryPort.deleteShoppingNote(noteId)
    }
}
