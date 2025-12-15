package com.mobile.iwbi.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * IWBI Brand Color Palette
 *
 * A cohesive color system that creates a strong brand identity
 * Primary: Deep Purple - Professional and trustworthy
 * Secondary: Mauve - Warm and inviting
 * Tertiary: Teal - Fresh and modern
 */

// Primary Brand Colors - Deep Purple (Professional & Trustworthy)
private val IWBIPrimary = Color(0xFF6750A4)           // Main brand color
private val IWBIPrimaryLight = Color(0xFF9E7BDB)      // Lighter variant
private val IWBIPrimaryDark = Color(0xFF4F378B)       // Darker variant
private val IWBIPrimaryContainer = Color(0xFFEADDFF)  // Container background

// Secondary Brand Colors - Mauve (Warm & Inviting)
private val IWBISecondary = Color(0xFF7D5260)         // Secondary brand
private val IWBISecondaryLight = Color(0xFFB08090)    // Lighter variant
private val IWBISecondaryDark = Color(0xFF5F2838)     // Darker variant
private val IWBISecondaryContainer = Color(0xFFFFD8E4)// Container background

// Tertiary Brand Colors - Teal (Fresh & Modern)
private val IWBITertiary = Color(0xFF009688)          // Tertiary accent
private val IWBITertiaryLight = Color(0xFF4DB6AC)     // Lighter variant
private val IWBITertiaryDark = Color(0xFF00695C)      // Darker variant
private val IWBITertiaryContainer = Color(0xFFE0F2F1)// Container background

// Semantic Colors - Status and Feedback
private val IWBISuccess = Color(0xFF4CAF50)           // Success states
private val IWBISuccessLight = Color(0xFF80E27E)      // Light success
private val IWBISuccessDark = Color(0xFF087F23)       // Dark success
private val IWBISuccessContainer = Color(0xFFE8F5E9)  // Success container

private val IWBIWarning = Color(0xFFFF9800)           // Warning states
private val IWBIWarningLight = Color(0xFFFFCC80)      // Light warning
private val IWBIWarningDark = Color(0xFFEF6C00)       // Dark warning
private val IWBIWarningContainer = Color(0xFFFFF3E0)  // Warning container

private val IWBIError = Color(0xFFE91E63)             // Error states
private val IWBIErrorLight = Color(0xFFF06292)        // Light error
private val IWBIErrorDark = Color(0xFFC2185B)         // Dark error
private val IWBIErrorContainer = Color(0xFFFFE5EC)    // Error container

private val IWBIInfo = Color(0xFF2196F3)              // Info states
private val IWBIInfoLight = Color(0xFF64B5F6)         // Light info
private val IWBIInfoDark = Color(0xFF1976D2)          // Dark info
private val IWBIInfoContainer = Color(0xFFE3F2FD)     // Info container

// Surface & Background Colors
private val IWBISurface = Color(0xFFFFFBFE)           // Main surface
private val IWBISurfaceVariant = Color(0xFFF3EDF7)    // Variant surface
private val IWBISurfaceTint = Color(0xFFEADDFF)       // Tinted surface
private val IWBIBackground = Color(0xFFFFFBFE)        // Main background

// Text & Content Colors
private val IWBIOnSurface = Color(0xFF1C1B1F)         // Text on surface
private val IWBIOnSurfaceVariant = Color(0xFF49454F)  // Secondary text
private val IWBIOnBackground = Color(0xFF1C1B1F)      // Text on background
private val IWBIOutline = Color(0xFF79747E)           // Borders & dividers
private val IWBIOutlineVariant = Color(0xFFCAC4D0)    // Subtle borders

// Light theme with enhanced brand consistency
private val LightColorScheme = lightColorScheme(
    // Primary brand colors
    primary = IWBIPrimary,
    onPrimary = Color.White,
    primaryContainer = IWBIPrimaryContainer,
    onPrimaryContainer = IWBIPrimaryDark,

    // Secondary brand colors
    secondary = IWBISecondary,
    onSecondary = Color.White,
    secondaryContainer = IWBISecondaryContainer,
    onSecondaryContainer = IWBISecondaryDark,

    // Tertiary brand colors
    tertiary = IWBITertiary,
    onTertiary = Color.White,
    tertiaryContainer = IWBITertiaryContainer,
    onTertiaryContainer = IWBITertiaryDark,

    // Error colors
    error = IWBIError,
    onError = Color.White,
    errorContainer = IWBIErrorContainer,
    onErrorContainer = IWBIErrorDark,

    // Background and surface colors
    background = IWBIBackground,
    onBackground = IWBIOnBackground,
    surface = IWBISurface,
    onSurface = IWBIOnSurface,
    surfaceVariant = IWBISurfaceVariant,
    onSurfaceVariant = IWBIOnSurfaceVariant,
    surfaceTint = IWBIPrimary,

    // Outline colors
    outline = IWBIOutline,
    outlineVariant = IWBIOutlineVariant,

    // Inverse colors for high contrast
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = IWBIPrimaryLight,

    // Container colors for better depth
    surfaceContainer = Color(0xFFF0EDF1),
    surfaceContainerHigh = Color(0xFFEAE7EC),
    surfaceContainerHighest = Color(0xFFE4E1E6),
    surfaceContainerLow = Color(0xFFF6F3F7),
    surfaceContainerLowest = Color.White
)

// Dark theme with enhanced brand consistency
private val DarkColorScheme = darkColorScheme(
    // Primary brand colors
    primary = IWBIPrimaryLight,
    onPrimary = IWBIPrimaryDark,
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = IWBIPrimaryContainer,

    // Secondary brand colors
    secondary = IWBISecondaryLight,
    onSecondary = IWBISecondaryDark,
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = IWBISecondaryContainer,

    // Tertiary brand colors
    tertiary = IWBITertiaryLight,
    onTertiary = IWBITertiaryDark,
    tertiaryContainer = Color(0xFF00504A),
    onTertiaryContainer = IWBITertiaryContainer,

    // Error colors
    error = IWBIErrorLight,
    onError = IWBIErrorDark,
    errorContainer = Color(0xFF8C1D32),
    onErrorContainer = IWBIErrorContainer,

    // Background and surface colors
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceTint = IWBIPrimaryLight,

    // Outline colors
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    // Inverse colors
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = IWBIPrimary,

    // Container colors for better depth
    surfaceContainer = Color(0xFF2B2930),
    surfaceContainerHigh = Color(0xFF36343B),
    surfaceContainerHighest = Color(0xFF413F46),
    surfaceContainerLow = Color(0xFF1C1B1F),
    surfaceContainerLowest = Color(0xFF0D0E0F)
)

/**
 * IWBI Theme with brand identity
 *
 * This theme provides consistent styling across the application with
 * professional color palette and enhanced visual hierarchy.
 *
 * @param darkTheme Whether to use dark theme
 * @param content The content to be themed
 */
@Composable
fun IWBITheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = IWBITypography,
        content = content
    )
}
