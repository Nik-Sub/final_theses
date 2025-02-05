package ai.cargominds.cm.persistence

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import org.jetbrains.exposed.sql.statements.StatementType
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

internal abstract class DatabaseTest {
    protected val logger = KotlinLogging.logger(javaClass.simpleName)

    protected val mysql: MySQLContainer<*> by lazy {
        MySQLContainer(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("cargominds")
            .withConfigurationOverride("mysql_conf_override")
            .withLogConsumer {
                logger.info { it.utf8String.trim() }
            }
    }

    @BeforeTest
    fun setup() {
        mysql.start()
    }

    @AfterTest
    fun tearDown() {
        mysql.stop()
    }

    protected fun getSchema(): Map<String, String> = transaction {
        val tables: List<String> = exec("SHOW TABLES", explicitStatementType = StatementType.SELECT) {
            buildList {
                while (it.next()) {
                    add(it.getString(1)!!)
                }
            }
        }!!

        tables.associateWith { tableName: String ->
            exec("SHOW CREATE TABLE $tableName", explicitStatementType = StatementType.SELECT) {
                it.next()
                it.getString(2)!!
            }!!
                .replace(" ENGINE=InnoDB", "")
                .replace(AUTO_INCREMENT_REGEX, "")
                .replace(CHARSET_REGEX, "")
                .replace(COLLATE_REGEX, "")
        }
    }
}

private val AUTO_INCREMENT_REGEX = """ AUTO_INCREMENT=\d+""".toRegex()
private val CHARSET_REGEX = """( DEFAULT)? CHARSET=[^, ]+""".toRegex()
private val COLLATE_REGEX = """( DEFAULT)? COLLATE([= ])[^, ]+""".toRegex()
