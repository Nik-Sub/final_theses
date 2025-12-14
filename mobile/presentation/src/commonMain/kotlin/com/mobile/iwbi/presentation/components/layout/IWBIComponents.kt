package com.mobile.iwbi.presentation.components.layout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardPadding

/**
 * Standard IWBI Screen Layout with consistent structure
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IWBIScreen(
    title: String,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = if (showTopBar) {
            {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        navigationIcon?.let { icon ->
                            IconButton(onClick = onNavigationClick ?: {}) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Navigation"
                                )
                            }
                        }
                    },
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        } else { {} },
        snackbarHost = {
            snackbarHostState?.let { SnackbarHost(it) }
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}

/**
 * Standard Content Container with consistent padding
 */
@Composable
fun IWBIContentContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(StandardPadding),
        content = content
    )
}

/**
 * Standard Card Component with brand styling
 */
@Composable
fun IWBICard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick ?: {},
        modifier = modifier,
        enabled = onClick != null && enabled,
        shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = IWBIDesignTokens.elevation_card
        ),
        content = {
            Column(
                modifier = Modifier.padding(StandardPadding),
                content = content
            )
        }
    )
}

/**
 * Standard Button with brand styling
 */
@Composable
fun IWBIButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    variant: ButtonVariant = ButtonVariant.PRIMARY
) {
    val colors = when (variant) {
        ButtonVariant.PRIMARY -> ButtonDefaults.buttonColors()
        ButtonVariant.SECONDARY -> ButtonDefaults.filledTonalButtonColors()
        ButtonVariant.OUTLINE -> ButtonDefaults.outlinedButtonColors()
        ButtonVariant.DANGER -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    }

    val buttonContent: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(IWBIDesignTokens.icon_size_small)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }

    when (variant) {
        ButtonVariant.PRIMARY, ButtonVariant.DANGER -> {
            Button(
                onClick = onClick,
                modifier = modifier.height(IWBIDesignTokens.button_height),
                enabled = enabled,
                colors = colors,
                shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m),
                content = { buttonContent() }
            )
        }
        ButtonVariant.SECONDARY -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = modifier.height(IWBIDesignTokens.button_height),
                enabled = enabled,
                colors = colors,
                shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m),
                content = { buttonContent() }
            )
        }
        ButtonVariant.OUTLINE -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.height(IWBIDesignTokens.button_height),
                enabled = enabled,
                colors = colors,
                shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m),
                content = { buttonContent() }
            )
        }
    }
}

/**
 * Empty State Component
 */
@Composable
fun IWBIEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(IWBIDesignTokens.icon_size_xl),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_s))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )

        action?.let {
            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xl))
            it()
        }
    }
}

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE,
    DANGER
}
