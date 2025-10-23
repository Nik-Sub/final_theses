package com.iwbi.domain.shopping

import kotlinx.datetime.Clock

data class ShoppingNote(
    val id: String,
    val title: String,
    val items: List<ShoppingItem>,
    val sharedWith: List<String> = emptyList(), // User IDs who have access to this note
    val createdBy: String,
    val lastModified: Long = Clock.System.now().toEpochMilliseconds()
)
