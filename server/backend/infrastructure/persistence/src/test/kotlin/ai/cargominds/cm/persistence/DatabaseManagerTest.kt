package ai.cargominds.cm.persistence

import ai.cargominds.cm.persistence.schemas.Schemas
import ai.cargominds.exposed.utils.DatabaseManager
import ai.cargominds.exposed.utils.DatabaseManagerConfig
import ai.cargominds.exposed.utils.migration.MigrationManager
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DatabaseManagerTest : DatabaseTest() {
    @Test
    fun `verify current schema`() {
        val dbManager = DatabaseManager(
            MigrationManager(migrations),
            DatabaseManagerConfig(
                url = mysql.jdbcUrl,
                driver = mysql.driverClassName,
                user = mysql.username,
                password = mysql.password
            ),
            databaseSchema
        )

        dbManager.initialize()

        assertEquals(Schemas.getVersion(databaseSchema.version).toSortedMap(), getSchema().toSortedMap())
    }
}
