package com.mobile.iwbi.presentation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import com.mobile.iwbi.domain.store.Store
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {
    val storeNavType = object : NavType<Store>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): Store? {
            val jsonString = bundle.getString(key) ?: return null
            return Json.decodeFromString(jsonString)
        }

        override fun parseValue(value: String): Store {
            return Json.decodeFromString(decodeURIComponent(value))
        }

        override fun put(bundle: Bundle, key: String, value: Store) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: Store): String {
            return encodeURIComponent(Json.encodeToString(value))
        }
    }

    fun encodeURIComponent(str: String): String {
        val unreserved = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~"
        val sb = StringBuilder()
        for (c in str) {
            if (c in unreserved) {
                sb.append(c)
            } else {
                val hex = c.code.toString(16).uppercase()
                sb.append('%')
                if (hex.length == 1) sb.append('0')
                sb.append(hex)
            }
        }
        return sb.toString()
    }

    fun decodeURIComponent(encoded: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < encoded.length) {
            val c = encoded[i]
            if (c == '%') {
                val hex = encoded.substring(i + 1, i + 3)
                val charCode = hex.toInt(16)
                sb.append(charCode.toChar())
                i += 3
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }
}
