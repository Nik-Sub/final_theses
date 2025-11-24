package com.iwbi.domain.shopping

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingNote(
    val id: String,
    val title: String,
    val items: List<ShoppingItem>,
    val sharedWith: List<String> = emptyList(),
    val createdBy: String = "",
    val lastModified: Long = Clock.System.now().toEpochMilliseconds()
)
