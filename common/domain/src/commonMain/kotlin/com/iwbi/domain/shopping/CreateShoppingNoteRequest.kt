package com.iwbi.domain.shopping

import kotlinx.serialization.Serializable

@Serializable
data class CreateShoppingNoteRequest(
    val title: String,
    val createdBy: String,
    val sharedWith: List<String> = emptyList()
)