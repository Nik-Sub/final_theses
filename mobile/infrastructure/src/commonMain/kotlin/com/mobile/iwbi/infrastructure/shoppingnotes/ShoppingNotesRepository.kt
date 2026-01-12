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
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import kotlinx.serialization.Serializable

class ShoppingNotesRepository(
    private val httpClientProvider: () -> HttpClient,
    private val authProvider: AuthenticationProviderPort,
) : ShoppingNotesRepositoryPort {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _shoppingNotes = MutableStateFlow<List<ShoppingNote>>(emptyList())

    private var currentUserId: String? = null
    private var pollingJob: kotlinx.coroutines.Job? = null

    init {
        // Observe user changes and clear state when user changes
        scope.launch {
            authProvider.observeCurrentUser().collect { user ->
                if (currentUserId != user?.uid) {
                    println("DEBUG: ShoppingNotesRepository - User changed from $currentUserId to ${user?.uid} - clearing state")

                    // Cancel previous polling
                    pollingJob?.cancel()
                    pollingJob = null

                    _shoppingNotes.value = emptyList()
                    currentUserId = user?.uid

                    // Start polling if we have a user
                    if (user != null) {
                        println("DEBUG: Starting polling for shopping notes for user: ${user.uid}")
                        startPolling()
                    }
                }
            }
        }
    }

    private fun startPolling() {
        pollingJob = scope.launch {
            // Initial refresh
            refresh()

            // Continuous polling every 10 seconds
            while (true) {
                kotlinx.coroutines.delay(1000) // 10 seconds
                try {
                    refresh()
                } catch (e: Exception) {
                    println("DEBUG: Shopping notes polling refresh failed: ${e.message}")
                }
            }
        }
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
                val notes = httpClientProvider().get("shopping-notes").body<List<ShoppingNote>>()
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
            val savedNote = httpClientProvider().post("shopping-notes") {
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
            httpClientProvider().put("shopping-notes/${note.id}") {
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
            httpClientProvider().delete("shopping-notes/$noteId")

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
            httpClientProvider().get("shopping-notes/$noteId").body<ShoppingNote?>()
        } catch (e: Exception) {
            println("Error getting shopping note: ${e.message}")
            // Fallback to local cache
            _shoppingNotes.value.find { it.id == noteId }
        }
    }
}
