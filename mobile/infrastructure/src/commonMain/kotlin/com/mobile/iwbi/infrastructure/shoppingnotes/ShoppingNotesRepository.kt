package com.mobile.iwbi.infrastructure.shoppingnotes

import com.iwbi.domain.shopping.CreateShoppingNoteRequest
import com.iwbi.domain.shopping.ShoppingNote
import com.mobile.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class ShoppingNotesRepository(
    private val httpClient: HttpClient,
) : ShoppingNotesRepositoryPort {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _shoppingNotes = MutableStateFlow<List<ShoppingNote>>(emptyList())

    init {
        // Load initial data when repository is created
        refresh() // No longer need to pass userId as it's handled server-side
    }

    override fun observeShoppingNotes(): StateFlow<List<ShoppingNote>> {
        return _shoppingNotes.map { notes ->
            notes // User filtering is now handled server-side
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun refresh() {
        scope.launch {
            try {
                val notes = httpClient.get("shopping-notes").body<List<ShoppingNote>>()
                _shoppingNotes.value = notes
            } catch (e: Exception) {
                // Keep existing state on error
                println("Error loading shopping notes: ${e.message}")
            }
        }
    }

    override fun observeShoppingNote(noteId: String): StateFlow<ShoppingNote?> {
        return _shoppingNotes.map { notes ->
            notes.find { note ->
                note.id == noteId
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    override suspend fun saveShoppingNote(note: ShoppingNote): ShoppingNote {
        return try {
            val request = CreateShoppingNoteRequest(
                title = note.title,
                createdBy = note.createdBy,
                sharedWith = note.sharedWith
            )

            println("Saving shopping note with request: $request")
            val savedNote = httpClient.post("shopping-notes") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<ShoppingNote>()

            // Update local state
            val currentNotes = _shoppingNotes.value.toMutableList()
            currentNotes.add(savedNote)
            _shoppingNotes.value = currentNotes

            savedNote
        } catch (e: Exception) {
            println("Error saving shopping note: ${e.message}")
            // Fallback: add to local state
            val currentNotes = _shoppingNotes.value.toMutableList()
            currentNotes.add(note)
            _shoppingNotes.value = currentNotes
            note
        }
    }

    override suspend fun updateShoppingNote(note: ShoppingNote) {
        try {
            httpClient.put("shopping-notes/${note.id}") {
                contentType(ContentType.Application.Json)
                setBody(note)
            }

            // Update local state immediately for reactive UI
            val currentNotes = _shoppingNotes.value.toMutableList()
            val index = currentNotes.indexOfFirst { it.id == note.id }
            if (index != -1) {
                currentNotes[index] = note
                _shoppingNotes.value = currentNotes
            }
        } catch (e: Exception) {
            println("Error updating shopping note: ${e.message}")
            // Still update local state for offline-like behavior
            val currentNotes = _shoppingNotes.value.toMutableList()
            val index = currentNotes.indexOfFirst { it.id == note.id }
            if (index != -1) {
                currentNotes[index] = note
                _shoppingNotes.value = currentNotes
            }
        }
    }

    override suspend fun deleteShoppingNote(noteId: String) {
        try {
            httpClient.delete("shopping-notes/$noteId")

            // Update local state immediately
            val currentNotes = _shoppingNotes.value.toMutableList()
            currentNotes.removeAll { it.id == noteId }
            _shoppingNotes.value = currentNotes
        } catch (e: Exception) {
            println("Error deleting shopping note: ${e.message}")
            // Still update local state
            val currentNotes = _shoppingNotes.value.toMutableList()
            currentNotes.removeAll { it.id == noteId }
            _shoppingNotes.value = currentNotes
        }
    }

    override suspend fun getShoppingNote(noteId: String): ShoppingNote? {
        return try {
            httpClient.get("shopping-notes/$noteId").body<ShoppingNote?>()
        } catch (e: Exception) {
            println("Error getting shopping note: ${e.message}")
            // Fallback to local cache
            _shoppingNotes.value.find { it.id == noteId }
        }
    }
}
