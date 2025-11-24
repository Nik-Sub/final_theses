package com.mobile.iwbi.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mobile.iwbi.presentation.MainPanel

@Composable
fun IWBIBottomBar(
    isSelected: (MainPanel) -> Boolean,
    onClick: (MainPanel) -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onClick(MainPanel.HOME) }) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (isSelected(MainPanel.HOME)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onClick(MainPanel.STORES) }) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = "Stores",
                    tint = if (isSelected(MainPanel.STORES)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onClick(MainPanel.FRIENDS) }) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Friends",
                    tint = if (isSelected(MainPanel.FRIENDS)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onClick(MainPanel.PROFILE) }) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = if (isSelected(MainPanel.PROFILE)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
