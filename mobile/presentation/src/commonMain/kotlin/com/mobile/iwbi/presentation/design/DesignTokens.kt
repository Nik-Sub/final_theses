package com.mobile.iwbi.presentation.design

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * IWBI Design System - Unified spacing, sizing, and brand values
 *
 * This design system provides a cohesive brand identity across all screens
 * with consistent spacing, colors, animations, and component styles.
 */
object IWBIDesignTokens {

    // ============================================================================
    // BRAND COLORS
    // ============================================================================

    /** Primary brand colors - Deep Purple palette */
    object BrandColors {
        val Primary = Color(0xFF6750A4)
        val PrimaryLight = Color(0xFF9E7BDB)
        val PrimaryDark = Color(0xFF4F378B)
        val PrimaryContainer = Color(0xFFEADDFF)

        val Secondary = Color(0xFF7D5260)
        val SecondaryLight = Color(0xFFB08090)
        val SecondaryDark = Color(0xFF5F2838)
        val SecondaryContainer = Color(0xFFFFD8E4)

        val Tertiary = Color(0xFF7D6E5F)
        val TertiaryLight = Color(0xFFB09F8F)
        val TertiaryDark = Color(0xFF5F4F41)
        val TertiaryContainer = Color(0xFFEEE1D3)

        val Success = Color(0xFF4CAF50)
        val SuccessLight = Color(0xFF80E27E)
        val SuccessDark = Color(0xFF087F23)
        val SuccessContainer = Color(0xFFE8F5E9)

        val Warning = Color(0xFFFF9800)
        val WarningLight = Color(0xFFFFCC80)
        val WarningDark = Color(0xFFEF6C00)
        val WarningContainer = Color(0xFFFFF3E0)

        val Error = Color(0xFFE91E63)
        val ErrorLight = Color(0xFFF06292)
        val ErrorDark = Color(0xFFC2185B)
        val ErrorContainer = Color(0xFFFFE5EC)

        val Info = Color(0xFF2196F3)
        val InfoLight = Color(0xFF64B5F6)
        val InfoDark = Color(0xFF1976D2)
        val InfoContainer = Color(0xFFE3F2FD)
    }

    /** Surface and background colors */
    object SurfaceColors {
        val Surface = Color(0xFFFFFBFE)
        val SurfaceVariant = Color(0xFFF3EDF7)
        val SurfaceTint = Color(0xFFEADDFF)
        val Background = Color(0xFFFFFBFE)
        val BackgroundSecondary = Color(0xFFF8F6FA)
    }

    /** Text and content colors */
    object ContentColors {
        val OnSurface = Color(0xFF1C1B1F)
        val OnSurfaceVariant = Color(0xFF49454F)
        val OnBackground = Color(0xFF1C1B1F)
        val Disabled = Color(0xFF79747E)
        val Outline = Color(0xFF79747E)
        val OutlineVariant = Color(0xFFCAC4D0)
    }

    // ============================================================================
    // SPACING SCALE
    // ============================================================================

    val space_xxs = 2.dp
    val space_xs = 4.dp
    val space_s = 8.dp
    val space_m = 16.dp      // Primary spacing unit
    val space_l = 24.dp
    val space_xl = 32.dp
    val space_xxl = 48.dp
    val space_xxxl = 64.dp

    // ============================================================================
    // COMPONENT DIMENSIONS
    // ============================================================================

    /** Button dimensions */
    object ButtonDimensions {
        val HeightLarge = 56.dp
        val HeightDefault = 48.dp
        val HeightSmall = 40.dp
        val HeightCompact = 32.dp
        val MinWidth = 88.dp
        val PaddingHorizontal = 24.dp
        val PaddingHorizontalSmall = 16.dp
    }

    /** FAB dimensions */
    object FabDimensions {
        val SizeLarge = 64.dp
        val SizeDefault = 56.dp
        val SizeSmall = 40.dp
        val Padding = 16.dp
    }

    /** Icon sizes */
    object IconSizes {
        val Tiny = 12.dp
        val Small = 16.dp
        val Default = 24.dp
        val Medium = 32.dp
        val Large = 48.dp
        val XLarge = 64.dp
        val Huge = 96.dp
    }

    /** Card dimensions */
    object CardDimensions {
        val MinHeight = 120.dp
        val PaddingDefault = space_m
        val PaddingLarge = space_l
        val PaddingSmall = space_s
    }

    /** List item heights */
    object ListItemHeights {
        val Compact = 48.dp
        val Default = 56.dp
        val Medium = 72.dp
        val Large = 88.dp
        val XLarge = 104.dp
    }

    /** App bar dimensions */
    object AppBarDimensions {
        val Height = 64.dp
        val BottomBarHeight = 80.dp
        val BottomBarHeightCompact = 64.dp
    }

    // ============================================================================
    // BORDER RADIUS
    // ============================================================================

    val corner_radius_xs = 4.dp
    val corner_radius_s = 8.dp
    val corner_radius_m = 12.dp
    val corner_radius_l = 16.dp
    val corner_radius_xl = 20.dp
    val corner_radius_xxl = 28.dp
    val corner_radius_round = 100.dp // For fully rounded corners

    // ============================================================================
    // ELEVATION & SHADOWS
    // ============================================================================

    val elevation_none = 0.dp
    val elevation_card = 2.dp
    val elevation_card_elevated = 4.dp
    val elevation_modal = 8.dp
    val elevation_fab = 6.dp
    val elevation_navigation = 3.dp
    val elevation_dialog = 24.dp

    // ============================================================================
    // BORDERS
    // ============================================================================

    val border_width_thin = 1.dp
    val border_width_medium = 2.dp
    val border_width_thick = 3.dp

    // ============================================================================
    // ANIMATION DURATIONS (in milliseconds)
    // ============================================================================

    object AnimationDurations {
        const val Instant = 0
        const val Quick = 150
        const val Normal = 250
        const val Emphasized = 300
        const val Slow = 500
        const val VerySlow = 800
    }

    // ============================================================================
    // ANIMATION SPECS
    // ============================================================================

    object Animations {
        // Standard spring animation for most interactions
        val StandardSpring = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )

        // Quick spring for immediate feedback
        val QuickSpring = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        )

        // Smooth spring for gentle animations
        val SmoothSpring = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )

        // Standard tween for linear animations
        val StandardTween = tween<Float>(durationMillis = AnimationDurations.Normal)

        // Quick tween for fast transitions
        val QuickTween = tween<Float>(durationMillis = AnimationDurations.Quick)

        // Emphasized tween for important transitions
        val EmphasizedTween = tween<Float>(durationMillis = AnimationDurations.Emphasized)
    }

    // ============================================================================
    // RESPONSIVE LAYOUT
    // ============================================================================

    val content_max_width = 600.dp
    val content_padding_horizontal = space_m
    val screen_padding_vertical = space_l

    // ============================================================================
    // LEGACY SUPPORT (keeping for backward compatibility)
    // ============================================================================

    @Deprecated("Use ButtonDimensions.HeightDefault", ReplaceWith("ButtonDimensions.HeightDefault"))
    val button_height = ButtonDimensions.HeightDefault

    @Deprecated("Use ButtonDimensions.HeightSmall", ReplaceWith("ButtonDimensions.HeightSmall"))
    val button_height_small = ButtonDimensions.HeightSmall

    @Deprecated("Use FabDimensions.SizeDefault", ReplaceWith("FabDimensions.SizeDefault"))
    val fab_size = FabDimensions.SizeDefault

    @Deprecated("Use FabDimensions.SizeSmall", ReplaceWith("FabDimensions.SizeSmall"))
    val fab_size_small = FabDimensions.SizeSmall

    @Deprecated("Use IconSizes.Small", ReplaceWith("IconSizes.Small"))
    val icon_size_small = IconSizes.Small

    @Deprecated("Use IconSizes.Default", ReplaceWith("IconSizes.Default"))
    val icon_size_default = IconSizes.Default

    @Deprecated("Use IconSizes.Large", ReplaceWith("IconSizes.Large"))
    val icon_size_large = IconSizes.Large

    @Deprecated("Use IconSizes.XLarge", ReplaceWith("IconSizes.XLarge"))
    val icon_size_xl = IconSizes.XLarge

    @Deprecated("Use CardDimensions.MinHeight", ReplaceWith("CardDimensions.MinHeight"))
    val card_min_height = CardDimensions.MinHeight

    @Deprecated("Use ListItemHeights.Default", ReplaceWith("ListItemHeights.Default"))
    val list_item_height = ListItemHeights.Medium

    @Deprecated("Use ListItemHeights.Compact", ReplaceWith("ListItemHeights.Compact"))
    val list_item_height_compact = ListItemHeights.Default

    @Deprecated("Use ListItemHeights.Large", ReplaceWith("ListItemHeights.Large"))
    val list_item_height_large = ListItemHeights.Large

    @Deprecated("Use AppBarDimensions.Height", ReplaceWith("AppBarDimensions.Height"))
    val app_bar_height = AppBarDimensions.Height

    @Deprecated("Use AppBarDimensions.BottomBarHeight", ReplaceWith("AppBarDimensions.BottomBarHeight"))
    val bottom_bar_height = AppBarDimensions.BottomBarHeight
}

/**
 * Standard spacing shortcuts for consistent padding and spacing
 */
val StandardPadding = IWBIDesignTokens.space_m
val StandardSpacing = IWBIDesignTokens.space_m
val StandardButtonHeight = IWBIDesignTokens.ButtonDimensions.HeightDefault
val StandardCornerRadius = IWBIDesignTokens.corner_radius_m
val StandardElevation = IWBIDesignTokens.elevation_card
val LargeCornerRadius = IWBIDesignTokens.corner_radius_l
val SmallCornerRadius = IWBIDesignTokens.corner_radius_s
