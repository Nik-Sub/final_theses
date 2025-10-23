package com.mobile.iwbi.presentation

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote

data class HomePanelUiState(
    val shoppingNotes: List<ShoppingNote> = emptyList(),
    val selectedNote: ShoppingNote? = null,
    val isLoading: Boolean = false,
    val currentUserId: String = "user1", // This should come from authentication
    val templates: List<List<ShoppingItem>> = listOf(
        listOf(ShoppingItem("Apples"), ShoppingItem("Oranges"), ShoppingItem("Bananas")),
        listOf(ShoppingItem("Rice"), ShoppingItem("Chicken"), ShoppingItem("Vegetables"))
    )
)