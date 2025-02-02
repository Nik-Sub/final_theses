package com.mobile.iwbi.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Panel {
    @Serializable
    data object LoginPanel : Panel

    @Serializable
    data object HomePanel : Panel
}