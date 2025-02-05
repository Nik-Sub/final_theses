package com.iwbi.gradleplugins.ktx

import java.io.File
import java.util.Properties

/**
 * Update [key] and [value] in the properties file
 *
 * @param key the key of the property for which to update its value.
 * @param value the new value to update to.
 */
fun File.setProperty(key: String, value: String) {
    setProperties(mapOf(key to value))
}

/**
 * Updates the properties file with the given properties. Existing properties will be updated. New properties will be added
 */
fun File.setProperties(properties: Map<String, String>) {
    val lineSeparator = System.lineSeparator()
    var content = ""

    val regexes = properties.keys.associateWith { key ->
        """^\s*$key\s*=.*$""".toRegex()
    }

    val pendingProperties = properties.toMutableMap()

    readLines().forEach { line ->
        var updated = false
        pendingProperties.toMap().forEach { (key, value) ->
            val regex = regexes[key]!!

            if (line.matches(regex)) {
                content += "$key=$value$lineSeparator"
                pendingProperties.remove(key)
                updated = true
            }
        }

        if (!updated) {
            content += "$line$lineSeparator"
        }
    }

    pendingProperties.forEach { (key, value) ->
        content += "$key=$value$lineSeparator"
    }

    writeText(content)
}

fun File.asProperties(): Properties? {
    return if (exists()) Properties().apply { inputStream().use { load(it) } } else null
}
