package com.mobile.iwbi.presentation.components.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mobile.iwbi.domain.store.Store
import com.mobile.iwbi.presentation.components.layout.IWBICard
import com.mobile.iwbi.presentation.components.layout.IWBIContentContainer
import com.mobile.iwbi.presentation.components.layout.IWBIEmptyState
import com.mobile.iwbi.presentation.components.layout.IWBIScreen
import com.mobile.iwbi.presentation.design.IWBIDesignTokens

@Composable
fun StorePanel(
    stores: List<Store>,
    onStoreSelected: (Store) -> Unit,
    modifier: Modifier = Modifier
) {
    IWBIScreen(
        title = "Stores",
        modifier = modifier
    ) { paddingValues ->
        IWBIContentContainer(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (stores.isEmpty()) {
                IWBIEmptyState(
                    icon = Icons.Default.Place,
                    title = "No Stores Available",
                    subtitle = "Stores will appear here when available"
                )
            } else {
                Text(
                    text = "Select a store to view details:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
                ) {
                    items(stores) { store ->
                        StoreCard(
                            store = store,
                            onClick = { onStoreSelected(store) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreCard(
    store: Store,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IWBICard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
        ) {
            // Store icon with brand styling
            Card(
                shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_s),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.size(IWBIDesignTokens.icon_size_large)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(IWBIDesignTokens.icon_size_default),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Store information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xs))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(IWBIDesignTokens.icon_size_small),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap to view store map",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Navigation indicator
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View store",
                modifier = Modifier.size(IWBIDesignTokens.icon_size_small),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}