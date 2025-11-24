package com.server.iwbi.persistence.shoppingnotes.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ShoppingItemsTable : LongIdTable("shopping_items") {
    val noteId = reference("note_id", ShoppingNotesTable, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 255)
    val isChecked = bool("is_checked").default(false)
}
