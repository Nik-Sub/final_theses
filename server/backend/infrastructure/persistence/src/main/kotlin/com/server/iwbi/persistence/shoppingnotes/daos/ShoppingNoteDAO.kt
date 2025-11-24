package com.server.iwbi.persistence.shoppingnotes.daos

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.server.iwbi.persistence.shoppingnotes.tables.ShoppingNotesTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ShoppingNoteDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ShoppingNoteDAO>(ShoppingNotesTable)

    var title by ShoppingNotesTable.title
    var createdBy by ShoppingNotesTable.createdBy
    var lastModified by ShoppingNotesTable.lastModified
}

fun ShoppingNoteDAO.toDomain(items: List<ShoppingItem>, sharedWith: List<String>): ShoppingNote {
    return ShoppingNote(
        id = id.value.toString(),
        title = title,
        items = items,
        createdBy = createdBy,
        sharedWith = sharedWith,
        lastModified = lastModified.toEpochMilli()
    )
}
