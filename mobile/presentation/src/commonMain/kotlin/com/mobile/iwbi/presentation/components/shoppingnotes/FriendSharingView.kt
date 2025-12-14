package com.mobile.iwbi.presentation.components.shoppingnotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iwbi.domain.shopping.ShoppingNote
import com.iwbi.domain.user.User
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardPadding

@Composable
fun FriendSharingView(
    note: ShoppingNote,
    friends: List<User>,
    selectedFriends: Set<String>,
    onToggleFriend: (User) -> Unit,
    onShareWithSelected: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(StandardPadding)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Share Shopping List",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel sharing"
                )
            }
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        // Note info card with consistent styling
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = IWBIDesignTokens.elevation_card),
            shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m)
        ) {
            Column(
                modifier = Modifier.padding(StandardPadding)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xs))
                Text(
                    text = "${note.items.size} items in this list",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        Text(
            text = "Select friends to share with:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

        // Friends list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
        ) {
            items(friends) { friend ->
                FriendSelectionItem(
                    friend = friend,
                    isSelected = selectedFriends.contains(friend.id),
                    onToggle = { onToggleFriend(friend) }
                )
            }

            if (friends.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(IWBIDesignTokens.space_xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(IWBIDesignTokens.icon_size_large),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))
                        Text(
                            text = "No friends yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Add friends to share your shopping lists!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        // Action buttons with consistent styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(IWBIDesignTokens.button_height),
                shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m)
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Button(
                onClick = onShareWithSelected,
                enabled = selectedFriends.isNotEmpty(),
                modifier = Modifier.weight(1f).height(IWBIDesignTokens.button_height),
                shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(IWBIDesignTokens.icon_size_small)
                )
                Spacer(modifier = Modifier.width(IWBIDesignTokens.space_xs))
                Text(
                    text = "Share (${selectedFriends.size})",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun FriendSelectionItem(
    friend: User,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = IWBIDesignTokens.elevation_card),
        shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(StandardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(IWBIDesignTokens.space_m))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (friend.email.isNotEmpty()) {
                    Text(
                        text = friend.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
