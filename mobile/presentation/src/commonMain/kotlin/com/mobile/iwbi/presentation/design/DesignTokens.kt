package com.mobile.iwbi.presentation.design

import androidx.compose.ui.unit.dp

/**
 * IWBI Design System - Unified spacing and sizing values
 */
object IWBIDesignTokens {
    // Consistent spacing scale
    val space_xs = 4.dp
    val space_s = 8.dp
    val space_m = 16.dp      // Primary spacing unit
    val space_l = 24.dp
    val space_xl = 32.dp
    val space_xxl = 48.dp

    // Standard dimensions
    val button_height = 48.dp
    val button_height_small = 36.dp
    val fab_size = 56.dp
    val fab_size_small = 40.dp
    val icon_size_small = 16.dp
    val icon_size_default = 24.dp
    val icon_size_large = 48.dp
    val icon_size_xl = 64.dp

    // Border radius
    val corner_radius_s = 8.dp
    val corner_radius_m = 12.dp
    val corner_radius_l = 16.dp
    val corner_radius_xl = 20.dp

    // Elevation
    val elevation_card = 2.dp
    val elevation_card_elevated = 4.dp
    val elevation_modal = 8.dp
    val elevation_fab = 6.dp

    // List item heights
    val list_item_height = 72.dp
    val list_item_height_compact = 56.dp
    val list_item_height_large = 88.dp

    // App bar dimensions
    val app_bar_height = 64.dp
    val bottom_bar_height = 80.dp

    // Content widths (for responsive design)
    val content_max_width = 600.dp
    val card_min_height = 120.dp
}

/**
 * Standard spacing shortcuts
 */
val StandardPadding = IWBIDesignTokens.space_m
val StandardSpacing = IWBIDesignTokens.space_m
val StandardButtonHeight = IWBIDesignTokens.button_height
val StandardCornerRadius = IWBIDesignTokens.corner_radius_m
val StandardElevation = IWBIDesignTokens.elevation_card
