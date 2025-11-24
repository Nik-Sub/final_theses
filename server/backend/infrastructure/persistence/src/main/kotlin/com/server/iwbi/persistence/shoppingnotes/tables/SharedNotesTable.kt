package com.server.iwbi.persistence.shoppingnotes.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SharedNotesTable : LongIdTable("shared_notes") {
    val noteId = reference("note_id", ShoppingNotesTable, onDelete = ReferenceOption.CASCADE)
    val userId = varchar("user_id", 255)

    init {
        uniqueIndex(noteId, userId)
    }
}
