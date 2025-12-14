package com.mobile.iwbi.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// IWBI Brand Colors - Consistent across all screens
private val IWBIBrandPrimary = Color(0xFF6750A4)      // Deep Purple - Primary brand color
private val IWBIBrandSecondary = Color(0xFF7D5260)     // Mauve - Secondary brand color
private val IWBIBrandTertiary = Color(0xFF7D6E5F)      // Brown - Accent color
private val IWBIBrandSuccess = Color(0xFF4CAF50)       // Green for success states
private val IWBIBrandWarning = Color(0xFFFF9800)       // Orange for warnings
private val IWBIBrandError = Color(0xFFE91E63)         // Pink-red for errors

// Light theme colors with enhanced brand consistency
private val LightColorScheme = lightColorScheme(
    // Primary brand colors
    primary = IWBIBrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),

    // Secondary brand colors
    secondary = IWBIBrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD8E4),
    onSecondaryContainer = Color(0xFF31111D),

    // Tertiary brand colors
    tertiary = IWBIBrandTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEEE1D3),
    onTertiaryContainer = Color(0xFF3F2E21),

    // Error colors with brand consistency
    error = IWBIBrandError,
    onError = Color.White,
    errorContainer = Color(0xFFFFE5EC),
    onErrorContainer = Color(0xFF3E001A),

    // Background and surface colors
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),

    // Outline colors
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    // Inverse colors
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFD0BCFF)
)

// Dark theme colors with enhanced brand consistency
private val DarkColorScheme = darkColorScheme(
    // Primary brand colors
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF371E73),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),

    // Secondary brand colors
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),

    // Tertiary brand colors
    tertiary = Color(0xFFE0D0C4),
    onTertiary = Color(0xFF3F2E21),
    tertiaryContainer = Color(0xFF564437),
    onTertiaryContainer = Color(0xFFEEE1D3),

    // Error colors with brand consistency
    error = Color(0xFFFFB3BA),
    onError = Color(0xFF5F1121),
    errorContainer = Color(0xFF8C1D32),
    onErrorContainer = Color(0xFFFFD9DE),

    // Background and surface colors
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    // Outline colors
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    // Inverse colors
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF6750A4)
)

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
