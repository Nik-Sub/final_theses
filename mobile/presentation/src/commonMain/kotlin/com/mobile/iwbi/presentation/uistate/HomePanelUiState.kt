package com.mobile.iwbi.presentation.uistate

import com.iwbi.domain.shopping.ShoppingItem

data class HomePanelUiState(
    val notes: List<ShoppingItem> = listOf(
        ShoppingItem("Milk"),
        ShoppingItem("Bread"),
        ShoppingItem("Eggs")
    ),
    val templates: List<List<ShoppingItem>> = listOf(
        listOf(ShoppingItem("Apples"), ShoppingItem("Oranges"), ShoppingItem("Bananas")),
        listOf(ShoppingItem("Rice"), ShoppingItem("Chicken"), ShoppingItem("Vegetables"))
    )
)