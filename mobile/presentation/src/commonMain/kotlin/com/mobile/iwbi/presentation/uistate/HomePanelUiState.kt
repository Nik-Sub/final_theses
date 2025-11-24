package com.mobile.iwbi.presentation.uistate

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote

data class HomePanelUiState(
    val shoppingNotes: List<ShoppingNote> = emptyList(),
    val selectedNote: ShoppingNote? = null,
    val isLoading: Boolean = false,
    val isEditingNote: Boolean = false,
    val newItemText: String = "",
    val templates: List<List<ShoppingItem>> = listOf(
        listOf(ShoppingItem("Rice"), ShoppingItem("Chicken"), ShoppingItem("Vegetables"))
    )
)