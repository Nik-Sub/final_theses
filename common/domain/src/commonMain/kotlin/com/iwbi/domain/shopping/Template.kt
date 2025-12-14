package com.iwbi.domain.shopping

import kotlinx.serialization.Serializable

@Serializable
data class Template(
    val name: String,
    val items: List<ShoppingItem>
)
