package com.server.iwbi.application.shoppingnotes

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.server.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.server.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.Clock

class ShoppingNotesService(
    private val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort
) : ShoppingNotesServicePort {

    override suspend fun getShoppingNotes(userId: String): List<ShoppingNote> {
        return shoppingNotesRepositoryPort.getShoppingNotesForUser(userId)
    }

    override suspend fun getShoppingNote(noteId: String, userId: String): ShoppingNote? {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return null
        return if (hasAccessToNote(note, userId)) note else null
    }

    override suspend fun createShoppingNote(title: String, createdBy: String, userIds: List<String>): ShoppingNote {
        val newNote = ShoppingNote(
            id = "",
            title = title,
            items = emptyList(),
            createdBy = createdBy,
            sharedWith = userIds,
            lastModified = Clock.System.now().toEpochMilliseconds()
        )
        
        return shoppingNotesRepositoryPort.saveShoppingNote(newNote)
    }

    override suspend fun updateShoppingNote(note: ShoppingNote, userId: String) {
        val existingNote = shoppingNotesRepositoryPort.getShoppingNote(note.id) ?: return
        if (!hasAccessToNote(existingNote, userId)) return

        val updatedNote = note.copy(lastModified = Clock.System.now().toEpochMilliseconds())
        shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
    }

    override suspend fun toggleItem(noteId: String, itemIndex: Int, userId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        if (!hasAccessToNote(note, userId)) return

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
        if (!hasAccessToNote(note, userId)) return

        val updatedNote = note.copy(
            items = note.items + item,
            lastModified = Clock.System.now().toEpochMilliseconds()
        )
        shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
    }

    override suspend fun removeItem(noteId: String, itemIndex: Int, userId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        if (!hasAccessToNote(note, userId)) return

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
        if (!hasAccessToNote(note, userId)) return

        if (!note.sharedWith.contains(newUserId)) {
            val updatedNote = note.copy(
                sharedWith = note.sharedWith + newUserId,
                lastModified = Clock.System.now().toEpochMilliseconds()
            )
            shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
        }
    }

    override suspend fun deleteShoppingNote(noteId: String, userId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        if (!hasAccessToNote(note, userId)) return

        shoppingNotesRepositoryPort.deleteShoppingNote(noteId)
    }

    private fun hasAccessToNote(note: ShoppingNote, userId: String): Boolean {
        return note.createdBy == userId || note.sharedWith.contains(userId)
    }
}
