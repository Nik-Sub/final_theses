package com.mobile.iwbi.presentation.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardPadding

/**
 * IWBI Branded Button Components
 *
 * Consistent button styles with brand identity across the application
 */

enum class IWBIButtonStyle {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    SUCCESS,
    WARNING,
    ERROR,
    OUTLINED,
    TEXT
}

enum class IWBIButtonSize {
    LARGE,
    DEFAULT,
    SMALL,
    COMPACT
}

@Composable
fun IWBIButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: IWBIButtonStyle = IWBIButtonStyle.PRIMARY,
    size: IWBIButtonSize = IWBIButtonSize.DEFAULT,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    fullWidth: Boolean = false
) {
    val height = when (size) {
        IWBIButtonSize.LARGE -> IWBIDesignTokens.ButtonDimensions.HeightLarge
        IWBIButtonSize.DEFAULT -> IWBIDesignTokens.ButtonDimensions.HeightDefault
        IWBIButtonSize.SMALL -> IWBIDesignTokens.ButtonDimensions.HeightSmall
        IWBIButtonSize.COMPACT -> IWBIDesignTokens.ButtonDimensions.HeightCompact
    }

    val horizontalPadding = when (size) {
        IWBIButtonSize.LARGE, IWBIButtonSize.DEFAULT -> IWBIDesignTokens.ButtonDimensions.PaddingHorizontal
        IWBIButtonSize.SMALL, IWBIButtonSize.COMPACT -> IWBIDesignTokens.ButtonDimensions.PaddingHorizontalSmall
    }

    val shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_m)

    val colors = when (style) {
        IWBIButtonStyle.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = IWBIDesignTokens.BrandColors.Primary,
            contentColor = Color.White
        )
        IWBIButtonStyle.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = IWBIDesignTokens.BrandColors.Secondary,
            contentColor = Color.White
        )
        IWBIButtonStyle.TERTIARY -> ButtonDefaults.buttonColors(
            containerColor = IWBIDesignTokens.BrandColors.Tertiary,
            contentColor = Color.White
        )
        IWBIButtonStyle.SUCCESS -> ButtonDefaults.buttonColors(
            containerColor = IWBIDesignTokens.BrandColors.Success,
            contentColor = Color.White
        )
        IWBIButtonStyle.WARNING -> ButtonDefaults.buttonColors(
            containerColor = IWBIDesignTokens.BrandColors.Warning,
            contentColor = Color.White
        )
        IWBIButtonStyle.ERROR -> ButtonDefaults.buttonColors(
            containerColor = IWBIDesignTokens.BrandColors.Error,
            contentColor = Color.White
        )
        IWBIButtonStyle.OUTLINED -> ButtonDefaults.outlinedButtonColors(
            contentColor = IWBIDesignTokens.BrandColors.Primary
        )
        IWBIButtonStyle.TEXT -> ButtonDefaults.textButtonColors(
            contentColor = IWBIDesignTokens.BrandColors.Primary
        )
    }

    val buttonModifier = modifier
        .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
        .height(height)

    when (style) {
        IWBIButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = shape,
                colors = colors,
                contentPadding = PaddingValues(horizontal = horizontalPadding)
            ) {
                ButtonContent(text, icon, size)
            }
        }
        IWBIButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = shape,
                colors = colors,
                contentPadding = PaddingValues(horizontal = horizontalPadding)
            ) {
                ButtonContent(text, icon, size)
            }
        }
        else -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = shape,
                colors = colors,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = IWBIDesignTokens.elevation_card
                )
            ) {
                ButtonContent(text, icon, size)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    size: IWBIButtonSize
) {
    val iconSize = when (size) {
        IWBIButtonSize.LARGE -> IWBIDesignTokens.IconSizes.Default
        IWBIButtonSize.DEFAULT -> IWBIDesignTokens.IconSizes.Small
        IWBIButtonSize.SMALL, IWBIButtonSize.COMPACT -> 14.dp
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = IWBIDesignTokens.space_xs)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.width(IWBIDesignTokens.space_s))
        }
        Text(
            text = text,
            style = when (size) {
                IWBIButtonSize.LARGE -> MaterialTheme.typography.titleMedium
                IWBIButtonSize.DEFAULT -> MaterialTheme.typography.labelLarge
                IWBIButtonSize.SMALL -> MaterialTheme.typography.labelMedium
                IWBIButtonSize.COMPACT -> MaterialTheme.typography.labelSmall
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Icon button with scale animation on press
 */
@Composable
fun IWBIIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = IWBIDesignTokens.IconSizes.Default
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.8f,
        animationSpec = IWBIDesignTokens.Animations.QuickSpring
    )

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.scale(scale)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else tint.copy(alpha = 0.38f),
            modifier = Modifier.size(size)
        )
    }
}

/**
 * Floating Action Button with brand styling
 */
@Composable
fun IWBIFloatingActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = false,
    text: String? = null,
    containerColor: Color = IWBIDesignTokens.BrandColors.Primary,
    contentColor: Color = Color.White
) {
    if (extended && text != null) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = modifier,
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = IWBIDesignTokens.elevation_fab
            ),
            shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_l)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(IWBIDesignTokens.IconSizes.Default)
            )
            Spacer(modifier = Modifier.width(IWBIDesignTokens.space_s))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier.size(IWBIDesignTokens.FabDimensions.SizeDefault),
            containerColor = containerColor,
            contentColor = contentColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = IWBIDesignTokens.elevation_fab
            ),
            shape = RoundedCornerShape(IWBIDesignTokens.corner_radius_l)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(IWBIDesignTokens.IconSizes.Default)
            )
        }
    }
}

