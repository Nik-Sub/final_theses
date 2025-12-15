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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.mobile.iwbi.presentation.components.layout.ButtonVariant
import com.mobile.iwbi.presentation.components.layout.IWBIButton
import com.mobile.iwbi.presentation.components.layout.IWBICard
import com.mobile.iwbi.presentation.components.layout.IWBIContentContainer
import com.mobile.iwbi.presentation.components.layout.IWBIEmptyState
import com.mobile.iwbi.presentation.components.layout.IWBIScreen
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.iwbi.domain.user.FriendRequest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestsPanel(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FriendsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Received", "Sent")

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
        title = "Friend Requests",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab selector
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content based on selected tab
            IWBIContentContainer {
                when (selectedTab) {
                    0 -> ReceivedRequestsTab(
                        requests = uiState.pendingRequests,
                        onAccept = viewModel::acceptFriendRequest,
                        onDecline = viewModel::declineFriendRequest
                    )
                    1 -> SentRequestsTab(
                        requests = uiState.sentRequests
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceivedRequestsTab(
    requests: List<FriendRequest>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (requests.isEmpty()) {
        IWBIEmptyState(
            icon = Icons.Default.Notifications,
            title = "No pending requests",
            subtitle = "You don't have any friend requests at the moment"
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
        ) {
            items(requests) { request ->
                FriendRequestItem(
                    request = request,
                    onAccept = { onAccept(request.id) },
                    onDecline = { onDecline(request.id) },
                    showActions = true,
                    isReceived = true
                )
            }
        }
    }
}

@Composable
private fun SentRequestsTab(
    requests: List<FriendRequest>,
    modifier: Modifier = Modifier
) {
    if (requests.isEmpty()) {
        IWBIEmptyState(
            icon = Icons.Default.Notifications,
            title = "No sent requests",
            subtitle = "You haven't sent any friend requests yet"
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
        ) {
            items(requests) { request ->
                FriendRequestItem(
                    request = request,
                    onAccept = { },
                    onDecline = { },
                    showActions = false,
                    isReceived = false
                )
            }
        }
    }
}

@Composable
private fun FriendRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    showActions: Boolean,
    isReceived: Boolean,
    modifier: Modifier = Modifier
) {
    IWBICard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
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

                // User information - Display email and status only
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    val displayUser = if (isReceived) request.fromUser else request.toUser

                    // Show email with ellipsis for long emails
                    Text(
                        text = displayUser.email,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xxs))

                    // Show status
                    Text(
                        text = if (showActions) "Wants to be your friend" else "Request pending",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action buttons - moved to separate row below for better layout
            if (showActions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
                ) {
                    IWBIButton(
                        text = "Accept",
                        onClick = onAccept,
                        icon = Icons.Default.Check,
                        variant = ButtonVariant.SECONDARY,
                        modifier = Modifier.weight(1f)
                    )
                    IWBIButton(
                        text = "Decline",
                        onClick = onDecline,
                        icon = Icons.Default.Close,
                        variant = ButtonVariant.OUTLINE,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
