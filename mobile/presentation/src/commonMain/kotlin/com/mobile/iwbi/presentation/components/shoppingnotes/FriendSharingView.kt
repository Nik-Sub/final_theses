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
import com.mobile.iwbi.presentation.components.IWBITopAppBar
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardPadding
import com.mobile.iwbi.presentation.design.components.IWBIButton
import com.mobile.iwbi.presentation.design.components.IWBIButtonStyle
import com.mobile.iwbi.presentation.design.components.IWBISearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendSharingView(
    note: ShoppingNote,
    friends: List<User>,
    selectedFriends: Set<String>,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onToggleFriend: (User) -> Unit,
    onRemoveFriend: (User) -> Unit = {},
    onShareWithSelected: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            IWBITopAppBar(
                headerTitle = "Share Shopping List",
                onLeadingIconClick = onCancel
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(StandardPadding)
        ) {

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

        // Search field
        IWBISearchField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Search friends...",
            onClear = { onSearchQueryChange("") }
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

        // Filter friends based on search query
        val filteredFriends = if (searchQuery.isBlank()) {
            friends
        } else {
            friends.filter { friend ->
                friend.displayName.contains(searchQuery, ignoreCase = true) ||
                friend.email.contains(searchQuery, ignoreCase = true)
            }
        }

        // Separate friends into two groups
        val alreadySharedFriends = filteredFriends.filter { friend ->
            note.sharedWith.contains(friend.id)
        }
        val availableFriends = filteredFriends.filter { friend ->
            !note.sharedWith.contains(friend.id)
        }

        // Friends list with sections
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
        ) {
            // Already Shared Section
            if (alreadySharedFriends.isNotEmpty()) {
                item {
                    Text(
                        text = "Already Shared (${alreadySharedFriends.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = IWBIDesignTokens.space_xs)
                    )
                }

                items(alreadySharedFriends) { friend ->
                    FriendSelectionItem(
                        friend = friend,
                        isSelected = selectedFriends.contains(friend.id),
                        onToggle = { onToggleFriend(friend) },
                        onRemove = { onRemoveFriend(friend) },
                        isAlreadyShared = true
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))
                }
            }

            // Available to Share Section
            if (availableFriends.isNotEmpty()) {
                item {
                    Text(
                        text = "Available to Share (${availableFriends.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = IWBIDesignTokens.space_xs)
                    )
                }

                items(availableFriends) { friend ->
                    FriendSelectionItem(
                        friend = friend,
                        isSelected = selectedFriends.contains(friend.id),
                        onToggle = { onToggleFriend(friend) },
                        isAlreadyShared = false
                    )
                }
            }

            // Empty state
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
            } else if (filteredFriends.isEmpty() && searchQuery.isNotBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(IWBIDesignTokens.space_xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(IWBIDesignTokens.icon_size_large),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))
                        Text(
                            text = "No friends found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Try a different search term",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else if (availableFriends.isEmpty() && alreadySharedFriends.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(IWBIDesignTokens.space_l),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "All your friends already have access to this note!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
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
            IWBIButton(
                text = "Cancel",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                style = IWBIButtonStyle.OUTLINED
            )

            IWBIButton(
                text = "Share (${selectedFriends.size})",
                onClick = onShareWithSelected,
                enabled = selectedFriends.isNotEmpty(),
                modifier = Modifier.weight(1f),
                style = IWBIButtonStyle.PRIMARY,
                icon = Icons.Default.Share
            )
        }
        }
    }
}

@Composable
private fun FriendSelectionItem(
    friend: User,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onRemove: () -> Unit = {},
    isAlreadyShared: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isAlreadyShared && isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                isAlreadyShared -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = IWBIDesignTokens.elevation_card),
        shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m),
        onClick = if (isAlreadyShared) { {} } else onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(StandardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show checkbox only for non-shared friends
            if (!isAlreadyShared) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggle() }
                )
                Spacer(modifier = Modifier.width(IWBIDesignTokens.space_m))
            }

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

            if (isAlreadyShared) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_s),
                    modifier = Modifier.padding(end = IWBIDesignTokens.space_s)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = IWBIDesignTokens.space_s,
                            vertical = IWBIDesignTokens.space_xs
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Already shared",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Shared",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Remove button for already shared friends
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove access",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (isSelected && !isAlreadyShared) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
