package com.mobile.iwbi.presentation

import com.mobile.iwbi.domain.store.Store
import kotlinx.serialization.Serializable

@Serializable
sealed interface Panel {
    @Serializable
    data object LoginPanel : Panel

    @Serializable
    data object RegisterPanel : Panel

    @Serializable
    data object HomePanel : Panel

    @Serializable
    data object StorePanel : Panel

    @Serializable
    data class StoreMapScreen(
        val store: Store
    ) : Panel

    // Friend management panels
    @Serializable
    data object FriendsPanel : Panel

    @Serializable
    data object FriendRequestsPanel : Panel

    @Serializable
    data object AddFriendPanel : Panel

    @Serializable
    data class ShareNotePanel(
        val noteId: String
    ) : Panel

    // Additional panels for the new structure
    @Serializable
    data object ProfilePanel : Panel
}

enum class MainPanel(val screen: Panel) {
    HOME(Panel.HomePanel),
    STORES(Panel.StorePanel),
    FRIENDS(Panel.FriendsPanel),
    PROFILE(Panel.ProfilePanel)
}