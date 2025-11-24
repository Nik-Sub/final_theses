package com.server.iwbi.persistence.database

import org.jetbrains.exposed.sql.Database
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DatabaseProvider(private val databaseManager: DatabaseManager) : ReadOnlyProperty<Any, Database> {
    override operator fun getValue(thisRef: Any, property: KProperty<*>): Database {
        return databaseManager.database
    }
}
