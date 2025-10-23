package com.mobile.iwbi.presentation

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

    // Additional panels for the new structure
    @Serializable
    data object ProfilePanel : Panel
}

enum class MainPanel(val screen: Panel) {
    HOME(Panel.HomePanel),
    STORES(Panel.StorePanel),
    PROFILE(Panel.ProfilePanel)
}