package com.server.iwbi.persistence.config

import com.server.iwbi.persistence.shoppingnotes.tables.SharedNotesTable
import com.server.iwbi.persistence.shoppingnotes.tables.ShoppingItemsTable
import com.server.iwbi.persistence.shoppingnotes.tables.ShoppingNotesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseInitializer {
    fun initialize(config: DatabaseConfig): Database {
        val database = Database.connect(
            url = config.url,
            driver = config.driver,
            user = config.user,
            password = config.password
        )

        // Create tables
        transaction {
            SchemaUtils.create(ShoppingNotesTable, ShoppingItemsTable, SharedNotesTable)
        }

        return database
    }
}
