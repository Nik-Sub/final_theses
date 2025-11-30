package com.server.iwbi.persistence.shoppingnotes

import com.iwbi.domain.shopping.ShoppingNote
import com.server.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import com.server.iwbi.persistence.database.DatabaseProvider
import com.server.iwbi.persistence.shoppingnotes.daos.SharedNoteDAO
import com.server.iwbi.persistence.shoppingnotes.daos.ShoppingItemDAO
import com.server.iwbi.persistence.shoppingnotes.daos.ShoppingNoteDAO
import com.server.iwbi.persistence.shoppingnotes.daos.toDomain
import com.server.iwbi.persistence.shoppingnotes.tables.SharedNotesTable
import com.server.iwbi.persistence.shoppingnotes.tables.ShoppingItemsTable
import com.server.iwbi.persistence.shoppingnotes.tables.ShoppingNotesTable
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ShoppingNotesRepository(
    databaseProvider: DatabaseProvider
) : ShoppingNotesRepositoryPort {

    private val database by databaseProvider

    override suspend fun getShoppingNotes(): List<ShoppingNote> {
        return transaction(database) {
            ShoppingNoteDAO.all().map { noteDAO ->
                val items = ShoppingItemDAO.find { ShoppingItemsTable.noteId eq noteDAO.id }
                    .map { it.toDomain() }
                val sharedWith = SharedNoteDAO.find { SharedNotesTable.noteId eq noteDAO.id }
                    .map { it.userId }
                noteDAO.toDomain(items, sharedWith)
            }
        }
    }

    override suspend fun getShoppingNotesForUser(userId: String): List<ShoppingNote> {
        return transaction(database) {
            val userNotes = ShoppingNoteDAO.find { ShoppingNotesTable.createdBy eq userId }.toList()
            val sharedNoteIds = SharedNotesTable
                .selectAll()
                .where { SharedNotesTable.userId eq userId }
                .map { it[SharedNotesTable.noteId] }
            val sharedNotes = if (sharedNoteIds.isNotEmpty()) {
                ShoppingNoteDAO.find { ShoppingNotesTable.id inList sharedNoteIds }.toList()
            } else {
                emptyList<ShoppingNoteDAO>()
            }

            (userNotes + sharedNotes).distinct().map { noteDAO ->
                val items = ShoppingItemDAO.find { ShoppingItemsTable.noteId eq noteDAO.id }
                    .map { it.toDomain() }
                val sharedWith = SharedNoteDAO.find { SharedNotesTable.noteId eq noteDAO.id }
                    .map { it.userId }
                noteDAO.toDomain(items, sharedWith)
            }
        }
    }

    override suspend fun getShoppingNote(noteId: String): ShoppingNote? {
        return transaction(database) {
            val id = try {
                noteId.toLong()
            } catch (e: NumberFormatException) {
                return@transaction null
            }

            ShoppingNoteDAO.findById(id)?.let { noteDAO ->
                val items = ShoppingItemDAO.find { ShoppingItemsTable.noteId eq noteDAO.id }
                    .map { it.toDomain() }
                val sharedWith = SharedNoteDAO.find { SharedNotesTable.noteId eq noteDAO.id }
                    .map { it.userId }
                noteDAO.toDomain(items, sharedWith)
            }
        }
    }

    override suspend fun saveShoppingNote(note: ShoppingNote): ShoppingNote {
        return transaction(database) {
            val noteDAO = if (note.id.isNotEmpty()) {
                val id = try {
                    note.id.toLong()
                } catch (e: NumberFormatException) {
                    // If ID is not a valid Long (e.g., UUID), treat as new entity
                    null
                }

                if (id != null) {
                    ShoppingNoteDAO.findById(id) ?: ShoppingNoteDAO.new(id) {
                        title = note.title
                        createdBy = note.createdBy
                        lastModified = java.time.Instant.ofEpochMilli(note.lastModified)
                    }
                } else {
                    // Create new entity when ID is not a valid Long
                    ShoppingNoteDAO.new {
                        title = note.title
                        createdBy = note.createdBy
                        lastModified = java.time.Instant.ofEpochMilli(note.lastModified)
                    }
                }
            } else {
                ShoppingNoteDAO.new {
                    title = note.title
                    createdBy = note.createdBy
                    lastModified = java.time.Instant.ofEpochMilli(note.lastModified)
                }
            }

            // Delete existing items and shared entries
            ShoppingItemDAO.find { ShoppingItemsTable.noteId eq noteDAO.id }.forEach { it.delete() }
            SharedNoteDAO.find { SharedNotesTable.noteId eq noteDAO.id }.forEach { it.delete() }

            // Create shopping items
            val itemDAOs = note.items.map { item ->
                ShoppingItemDAO.new {
                    this.noteId = noteDAO.id
                    name = item.name
                    isChecked = item.isChecked
                }
            }

            // Create shared entries
            note.sharedWith.forEach { userId ->
                SharedNoteDAO.new {
                    this.noteId = noteDAO.id
                    this.userId = userId
                }
            }

            val items = itemDAOs.map { it.toDomain() }
            noteDAO.toDomain(items, note.sharedWith)
        }
    }

    override suspend fun updateShoppingNote(note: ShoppingNote) {
        transaction(database) {
            val id = try {
                note.id.toLong()
            } catch (e: NumberFormatException) {
                // If ID is not a valid Long (e.g., UUID), we cannot update
                return@transaction
            }

            ShoppingNoteDAO.findById(id)?.let { noteDAO ->
                // Update note properties
                noteDAO.title = note.title
                noteDAO.createdBy = note.createdBy
                noteDAO.lastModified = java.time.Instant.ofEpochMilli(note.lastModified)

                // Delete existing items and shared entries
                ShoppingItemDAO.find { ShoppingItemsTable.noteId eq noteDAO.id }.forEach { it.delete() }
                SharedNoteDAO.find { SharedNotesTable.noteId eq noteDAO.id }.forEach { it.delete() }

                // Create new items
                note.items.forEach { item ->
                    ShoppingItemDAO.new {
                        noteId = noteDAO.id
                        name = item.name
                        isChecked = item.isChecked
                    }
                }

                // Create new shared entries
                note.sharedWith.forEach { userId ->
                    SharedNoteDAO.new {
                        noteId = noteDAO.id
                        this.userId = userId
                    }
                }
            }
        }
    }

    override suspend fun deleteShoppingNote(noteId: String) {
        transaction(database) {
            val id = try {
                noteId.toLong()
            } catch (e: NumberFormatException) {
                // If ID is not a valid Long (e.g., UUID), we cannot delete
                return@transaction
            }

            ShoppingNoteDAO.findById(id)?.delete()
            // Items and shared entries will be deleted automatically due to CASCADE
        }
    }
}
