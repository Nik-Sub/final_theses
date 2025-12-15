package com.mobile.iwbi.application.shoppingnotes

import com.benasher44.uuid.uuid4
import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.datetime.Clock

class ShoppingNotesService(
    private val shoppingNotesRepositoryPort: ShoppingNotesRepositoryPort,
    private val authenticationServicePort: AuthenticationServicePort
) : ShoppingNotesServicePort {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun observeShoppingNotes(): StateFlow<List<ShoppingNote>> {
        return shoppingNotesRepositoryPort.observeShoppingNotes()
    }

    override fun observeShoppingNote(noteId: String): StateFlow<ShoppingNote?> {
        return shoppingNotesRepositoryPort.observeShoppingNote(noteId)
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

    override suspend fun updateShoppingNote(note: ShoppingNote) {
        val updatedNote = note.copy(lastModified = Clock.System.now().toEpochMilliseconds())
        shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
    }

    override suspend fun toggleItem(noteId: String, itemIndex: Int) {
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

    override suspend fun addItem(noteId: String, item: ShoppingItem) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        val updatedNote = note.copy(
            items = note.items + item,
            lastModified = Clock.System.now().toEpochMilliseconds()
        )
        shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
    }

    override suspend fun removeItem(noteId: String, itemIndex: Int) {
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

    override suspend fun shareNoteWithUser(noteId: String, newUserId: String) {
        val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return
        if (!note.sharedWith.contains(newUserId)) {
            val updatedNote = note.copy(
                sharedWith = note.sharedWith + newUserId,
                lastModified = Clock.System.now().toEpochMilliseconds()
            )
            shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
        }
    }

    override suspend fun shareNoteWithFriends(noteId: String, friendIds: List<String>): Result<Unit> {
        return try {
            val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return Result.failure(Exception("Note not found"))
            val updatedNote = note.copy(
                sharedWith = (note.sharedWith + friendIds).distinct(),
                lastModified = Clock.System.now().toEpochMilliseconds()
            )
            shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFriendFromNote(noteId: String, friendId: String): Result<Unit> {
        return try {
            val note = shoppingNotesRepositoryPort.getShoppingNote(noteId) ?: return Result.failure(Exception("Note not found"))
            val updatedNote = note.copy(
                sharedWith = note.sharedWith.filter { it != friendId },
                lastModified = Clock.System.now().toEpochMilliseconds()
            )
            shoppingNotesRepositoryPort.updateShoppingNote(updatedNote)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeShareableNotes(): StateFlow<List<ShoppingNote>> {
        // Return notes created by the current user that can be shared
        return combine(
            shoppingNotesRepositoryPort.observeShoppingNotes(),
            authenticationServicePort.observeCurrentUser()
        ) { notes, currentUser ->
            notes.filter { note ->
                note.createdBy == currentUser?.uid
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    override fun observeNotesSharedWithMe(): StateFlow<List<ShoppingNote>> {
        // Return notes shared with the current user
        return combine(
            shoppingNotesRepositoryPort.observeShoppingNotes(),
            authenticationServicePort.observeCurrentUser()
        ) { notes, currentUser ->
            notes.filter { note ->
                note.sharedWith.contains(currentUser?.uid)
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    override suspend fun deleteShoppingNote(noteId: String) {
        shoppingNotesRepositoryPort.deleteShoppingNote(noteId)
    }
}
