package ai.cargominds.cm.persistence.schemas

import java.util.TreeMap

internal object Schemas {
    fun getVersion(version: Int): Map<String, String> {
        return when (version) {
            1 -> schemaV1
            2 -> schemaV2
            3 -> schemaV3
            4 -> schemaV4
            5 -> schemaV5
            6 -> schemaV6
            7 -> schemaV7
            8 -> schemaV8
            else -> error("Unknown schema version: $version")
        }.toMap()
    }
}

class Schema private constructor(
    private val tableMap: MutableMap<String, String> = TreeMap(),
) : Map<String, String> by tableMap {
    fun addTable(table: String, value: String) {
        println("addTable($table, $value)")
        require(tableMap.put(table, value) == null) { "Table $table already defined" }
    }

    fun updateTable(table: String, transform: (old: String) -> String) {
        val oldTable = requireNotNull(tableMap[table]) { "No existing definition of table $table" }
        tableMap[table] = transform(oldTable)
    }

    fun removeTable(table: String) {
        tableMap.remove(table)
    }

    fun extend(config: Schema.() -> Unit): Schema {
        return Schema().apply {
            this.tableMap.putAll(this@Schema.tableMap)
            config()
        }
    }

    companion object {
        fun schema(block: Schema.() -> Unit): Schema {
            return Schema().apply(block)
        }
    }
}
