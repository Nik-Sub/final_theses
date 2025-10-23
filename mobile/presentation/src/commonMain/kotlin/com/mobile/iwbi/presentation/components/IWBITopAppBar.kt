package com.mobile.iwbi.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IWBITopAppBar(
    headerTitle: String,
    route: String?,
    onLeadingIconClick: () -> Unit
) {
    TopAppBar(
        title = { Text(headerTitle) },
        navigationIcon = {
            if (onLeadingIconClick != {}) {
                IconButton(onClick = onLeadingIconClick) {
                    // Add back arrow icon here
                    Text("←") // Placeholder - you can replace with proper back icon
                }
            }
        }
    )
}
