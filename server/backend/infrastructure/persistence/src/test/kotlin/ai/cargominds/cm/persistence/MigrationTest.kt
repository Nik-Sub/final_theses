package ai.cargominds.cm.persistence

import ai.cargominds.cm.persistence.schemas.Schemas
import ai.cargominds.exposed.utils.DatabaseManager
import ai.cargominds.exposed.utils.DatabaseManagerConfig
import ai.cargominds.exposed.utils.Schema
import ai.cargominds.exposed.utils.migration.Migration
import ai.cargominds.exposed.utils.migration.MigrationManager
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

internal abstract class MigrationTest(private val migration: Migration) : DatabaseTest() {
    private val schema: Schema = Schema(
        migration.toVersion,
        emptyList()
    )

    @BeforeTest
    fun initSchema() {
        Database.connect(mysql.jdbcUrl, mysql.driverClassName, mysql.username, mysql.password)

        transaction {
            exec("SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0")
            exec("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0")
            exec("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'")
            exec("SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0")

            Schemas.getVersion(migration.fromVersion).forEach { (tableName, createStatement) ->
                logger.info { "Creating table $tableName" }
                exec(createStatement)
            }

            exec("SET SQL_MODE=@OLD_SQL_MODE")
            exec("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS")
            exec("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS")
            exec("SET SQL_NOTES=@OLD_SQL_NOTES")

            assertEquals(Schemas.getVersion(migration.fromVersion), getSchema())

            exec("INSERT INTO Properties VALUES(1, 'schema_version', '${migration.fromVersion}');")

            initData()
        }

        TransactionManager.currentOrNull()?.connection?.close()
        TransactionManager.resetCurrent(null)
    }

    open fun Transaction.initData() = Unit

    @Test
    fun `validate migration`() {
        logger.info { "starting `validate migration`" }
        val dbManager = DatabaseManager(
            MigrationManager(migrations),
            DatabaseManagerConfig(
                url = mysql.jdbcUrl,
                driver = mysql.driverClassName,
                user = mysql.username,
                password = mysql.password
            ),
            schema
        )

        dbManager.initialize()

        // Verify result of migration
        assertEquals(Schemas.getVersion(migration.toVersion).toSortedMap(), getSchema().toSortedMap())

        transaction {
            validateData()
        }
    }

    open fun Transaction.validateData() = Unit
}
