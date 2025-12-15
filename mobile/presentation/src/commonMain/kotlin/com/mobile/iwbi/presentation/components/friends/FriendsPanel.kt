package com.mobile.iwbi.presentation.components.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iwbi.domain.user.User
import com.mobile.iwbi.presentation.components.IWBITopAppBar
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardPadding
import com.mobile.iwbi.presentation.design.components.IWBIListItemCard
import com.mobile.iwbi.presentation.design.components.IWBICardStyle
import com.mobile.iwbi.presentation.uistate.FriendsUiState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsPanel(
    onNavigateToAddFriend: () -> Unit,
    onNavigateToFriendRequests: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FriendsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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

    Scaffold(
        topBar = {
            IWBITopAppBar(
                headerTitle = "Friends"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
            ) {
                // Friend requests FAB
                FloatingActionButton(
                    onClick = onNavigateToFriendRequests,
                    shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = IWBIDesignTokens.space_s)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Friend Requests"
                        )
                        if (uiState.pendingRequests.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(IWBIDesignTokens.space_xs))
                            Text(
                                text = uiState.pendingRequests.size.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Add friend FAB
                FloatingActionButton(
                    onClick = onNavigateToAddFriend,
                    shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Friend"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(StandardPadding)
        ) {

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.friends.isEmpty() -> {
                    EmptyFriendsState(
                        onAddFriendClick = onNavigateToAddFriend,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    FriendsList(
                        friends = uiState.friends,
                        onRemoveFriend = viewModel::removeFriend,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendsList(
    friends: List<User>,
    onRemoveFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
    ) {
        items(friends) { friend ->
            FriendItem(
                friend = friend,
                onRemoveFriend = { onRemoveFriend(friend.id) }
            )
        }
    }
}

@Composable
private fun FriendItem(
    friend: User,
    onRemoveFriend: () -> Unit,
    modifier: Modifier = Modifier
) {
    IWBIListItemCard(
        title = friend.displayName,
        subtitle = if (friend.email.isNotEmpty()) friend.email else null,
        leadingIcon = Icons.Default.Person,
        modifier = modifier,
        style = IWBICardStyle.ELEVATED,
        trailingContent = {
            IconButton(onClick = onRemoveFriend) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Friend",
                    tint = IWBIDesignTokens.BrandColors.Error
                )
            }
        }
    )
}

@Composable
private fun EmptyFriendsState(
    onAddFriendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(IWBIDesignTokens.icon_size_large * 1.5f),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        Text(
            text = "No Friends Yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_s))

        Text(
            text = "Start by adding friends to share shopping lists",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}
