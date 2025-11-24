package com.server.iwbi.persistence.shoppingnotes.daos

import com.iwbi.domain.shopping.ShoppingItem
import com.server.iwbi.persistence.shoppingnotes.tables.ShoppingItemsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ShoppingItemDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ShoppingItemDAO>(ShoppingItemsTable)

    var noteId by ShoppingItemsTable.noteId
    var name by ShoppingItemsTable.name
    var isChecked by ShoppingItemsTable.isChecked
}

fun ShoppingItemDAO.toDomain(): ShoppingItem {
    return ShoppingItem(
        name = name,
        isChecked = isChecked
    )
}
