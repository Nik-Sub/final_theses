package com.mobile.iwbi.presentation.components.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import com.iwbi.domain.user.User
import com.mobile.iwbi.presentation.components.layout.ButtonVariant
import com.mobile.iwbi.presentation.components.layout.IWBIButton
import com.mobile.iwbi.presentation.components.layout.IWBICard
import com.mobile.iwbi.presentation.components.layout.IWBIContentContainer
import com.mobile.iwbi.presentation.components.layout.IWBIEmptyState
import com.mobile.iwbi.presentation.components.layout.IWBIScreen
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddFriendPanel(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FriendsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Show error messages via snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    IWBIScreen(
        title = "Add Friends",
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
        onNavigationClick = onNavigateBack,
        actions = {
            IconButton(onClick = { viewModel.refreshAllData() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    ) { paddingValues ->
        IWBIContentContainer(
            modifier = Modifier.padding(paddingValues)
        ) {
            // Search section
            Text(
                text = "Search for friends by email:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { query ->
                    viewModel.updateSearchQuery(query)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Enter email address...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.clearSearchResults()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchUsers(uiState.searchQuery)
                        keyboardController?.hide()
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

            IWBIButton(
                text = "Search",
                onClick = {
                    viewModel.searchUsers(uiState.searchQuery)
                    keyboardController?.hide()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.searchQuery.isNotEmpty() && !uiState.isLoading,
                icon = Icons.Default.Search
            )

            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xl))

            // Results section
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Searching for users...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                uiState.searchResults.isNotEmpty() -> {
                    Text(
                        text = "Search Results:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
                    ) {
                        items(uiState.searchResults) { user ->
                            UserSearchResultItem(
                                user = user,
                                isAlreadyFriend = uiState.friends.any { it.id == user.id },
                                onSendFriendRequest = { viewModel.sendFriendRequest(user.id) }
                            )
                        }
                    }
                }

                // Show "No users found" only if we have an empty query or search was performed
                uiState.searchQuery.isNotEmpty() && uiState.searchResults.isEmpty() && !uiState.isLoading -> {
                    IWBIEmptyState(
                        icon = Icons.Default.Person,
                        title = "No users found",
                        subtitle = "Try searching with a different email address"
                    )
                }

                // Show initial state when no search query has been entered
                else -> {
                    IWBIEmptyState(
                        icon = Icons.Default.Person,
                        title = "Find Friends",
                        subtitle = "Enter an email address above to search for friends to add"
                    )
                }
            }
        }
    }
}

@Composable
private fun UserSearchResultItem(
    user: User,
    isAlreadyFriend: Boolean,
    onSendFriendRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    IWBICard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
        ) {
            // User avatar placeholder
            Box(
                modifier = Modifier
                    .size(IWBIDesignTokens.icon_size_large)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User avatar",
                    modifier = Modifier.size(IWBIDesignTokens.icon_size_default),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // User information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action button
            if (isAlreadyFriend) {
                Text(
                    text = "Already friends",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                IWBIButton(
                    text = "Add",
                    onClick = onSendFriendRequest,
                    icon = Icons.Default.Add,
                    variant = ButtonVariant.SECONDARY
                )
            }
        }
    }
}
