package com.server.iwbi.persistence.shoppingnotes.daos

import com.server.iwbi.persistence.shoppingnotes.tables.SharedNotesTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SharedNoteDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SharedNoteDAO>(SharedNotesTable)

    var noteId by SharedNotesTable.noteId
    var userId by SharedNotesTable.userId
}
