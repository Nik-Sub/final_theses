package com.mobile.iwbi.presentation.navigation

import com.mobile.iwbi.domain.store.Store
import kotlinx.serialization.Serializable

@Serializable
sealed interface Panel {
    @Serializable
    data object LoginPanel : Panel

    @Serializable
    data object HomePanel : Panel

    @Serializable
    data object StorePanel : Panel

    @Serializable
    data class StoreMapScreen(
        val store: Store
    ) : Panel
}