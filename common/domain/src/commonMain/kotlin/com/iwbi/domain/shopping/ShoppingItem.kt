package com.iwbi.domain.shopping

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingItem(val name: String, var isChecked: Boolean = false)