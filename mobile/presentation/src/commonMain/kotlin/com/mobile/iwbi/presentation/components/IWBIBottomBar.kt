package com.mobile.iwbi.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile.iwbi.presentation.MainPanel
import com.mobile.iwbi.presentation.design.IWBIDesignTokens

/**
 * IWBI Bottom Navigation Bar with brand styling
 *
 * Features:
 * - Filled/outlined icon variants for selected/unselected states
 * - Label animations
 * - Brand color consistency
 * - Smooth animations
 */
@Composable
fun IWBIBottomBar(
    isSelected: (MainPanel) -> Boolean,
    onClick: (MainPanel) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(IWBIDesignTokens.AppBarDimensions.BottomBarHeight),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = IWBIDesignTokens.elevation_navigation
    ) {
        BottomBarItem(
            selected = isSelected(MainPanel.HOME),
            onClick = { onClick(MainPanel.HOME) },
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            label = "Home"
        )

        BottomBarItem(
            selected = isSelected(MainPanel.STORES),
            onClick = { onClick(MainPanel.STORES) },
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            label = "Stores"
        )

        BottomBarItem(
            selected = isSelected(MainPanel.FRIENDS),
            onClick = { onClick(MainPanel.FRIENDS) },
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            label = "Friends"
        )

        BottomBarItem(
            selected = isSelected(MainPanel.PROFILE),
            onClick = { onClick(MainPanel.PROFILE) },
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            label = "Profile"
        )
    }
}

@Composable
private fun RowScope.BottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1f,
        animationSpec = IWBIDesignTokens.Animations.QuickSpring
    )

    val iconColor by animateColorAsState(
        targetValue = if (selected) {
            IWBIDesignTokens.BrandColors.Primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(IWBIDesignTokens.AnimationDurations.Normal)
    )

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = IWBIDesignTokens.space_xxs)
            ) {
                Box(
                    modifier = Modifier
                        .scale(iconScale)
                        .then(
                            if (selected) {
                                Modifier
                                    .clip(RoundedCornerShape(IWBIDesignTokens.corner_radius_s))
                                    .background(
                                        IWBIDesignTokens.BrandColors.PrimaryContainer.copy(alpha = 0.3f)
                                    )
                                    .padding(IWBIDesignTokens.space_xs)
                            } else {
                                Modifier.padding(IWBIDesignTokens.space_xs)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (selected) selectedIcon else unselectedIcon,
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(IWBIDesignTokens.IconSizes.Default)
                    )
                }

                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xxs))

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = iconColor
                )
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = IWBIDesignTokens.BrandColors.Primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedTextColor = IWBIDesignTokens.BrandColors.Primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = Color.Transparent
        )
    )
}
