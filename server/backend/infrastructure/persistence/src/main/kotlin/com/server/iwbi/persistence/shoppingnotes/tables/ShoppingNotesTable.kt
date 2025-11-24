package com.server.iwbi.persistence.shoppingnotes.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object ShoppingNotesTable : LongIdTable("shopping_notes") {
    val title = varchar("title", 255)
    val createdBy = varchar("created_by", 255)
    val lastModified = timestamp("last_modified").default(Instant.now())
}
