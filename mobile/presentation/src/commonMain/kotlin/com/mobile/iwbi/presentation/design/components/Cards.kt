package com.mobile.iwbi.presentation.design.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardCornerRadius
import com.mobile.iwbi.presentation.design.StandardPadding

/**
 * IWBI Branded Card Components
 */

enum class IWBICardStyle {
    DEFAULT,
    ELEVATED,
    OUTLINED,
    FILLED,
    GRADIENT,
    SUCCESS,
    WARNING,
    ERROR,
    INFO
}

@Composable
fun IWBICard(
    modifier: Modifier = Modifier,
    style: IWBICardStyle = IWBICardStyle.DEFAULT,
    onClick: (() -> Unit)? = null,
    elevation: Dp = IWBIDesignTokens.elevation_card,
    cornerRadius: Dp = StandardCornerRadius,
    padding: Dp = StandardPadding,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val colors = when (style) {
        IWBICardStyle.DEFAULT -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        IWBICardStyle.ELEVATED -> CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        IWBICardStyle.OUTLINED -> CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        IWBICardStyle.FILLED -> CardDefaults.cardColors(
            containerColor = IWBIDesignTokens.BrandColors.PrimaryContainer
        )
        IWBICardStyle.GRADIENT -> CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
        IWBICardStyle.SUCCESS -> CardDefaults.cardColors(
            containerColor = IWBIDesignTokens.BrandColors.SuccessContainer
        )
        IWBICardStyle.WARNING -> CardDefaults.cardColors(
            containerColor = IWBIDesignTokens.BrandColors.WarningContainer
        )
        IWBICardStyle.ERROR -> CardDefaults.cardColors(
            containerColor = IWBIDesignTokens.BrandColors.ErrorContainer
        )
        IWBICardStyle.INFO -> CardDefaults.cardColors(
            containerColor = IWBIDesignTokens.BrandColors.InfoContainer
        )
    }

    val cardElevation = when (style) {
        IWBICardStyle.ELEVATED -> CardDefaults.elevatedCardElevation(
            defaultElevation = IWBIDesignTokens.elevation_card_elevated
        )
        IWBICardStyle.OUTLINED -> CardDefaults.outlinedCardElevation()
        else -> CardDefaults.cardElevation(defaultElevation = elevation)
    }

    val border = if (style == IWBICardStyle.OUTLINED) {
        BorderStroke(IWBIDesignTokens.border_width_thin, IWBIDesignTokens.ContentColors.Outline)
    } else null

    Card(
        modifier = modifier
            .then(if (style == IWBICardStyle.GRADIENT) {
                Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            IWBIDesignTokens.BrandColors.PrimaryLight.copy(alpha = 0.1f),
                            IWBIDesignTokens.BrandColors.Primary.copy(alpha = 0.05f)
                        )
                    ),
                    shape = shape
                )
            } else Modifier),
        shape = shape,
        colors = colors,
        elevation = cardElevation,
        border = border
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier)
                .padding(padding)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun IWBIFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: IWBICardStyle = IWBICardStyle.ELEVATED,
    iconTint: Color = IWBIDesignTokens.BrandColors.Primary
) {
    IWBICard(
        modifier = modifier,
        style = style,
        onClick = onClick,
        padding = IWBIDesignTokens.space_l
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(IWBIDesignTokens.IconSizes.Large)
                .padding(bottom = IWBIDesignTokens.space_m),
            tint = iconTint
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xs))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun IWBIListItemCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    style: IWBICardStyle = IWBICardStyle.DEFAULT
) {
    IWBICard(
        modifier = modifier,
        style = style,
        onClick = onClick,
        padding = IWBIDesignTokens.space_m
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(IWBIDesignTokens.IconSizes.Default)
                        .padding(end = IWBIDesignTokens.space_m),
                    tint = IWBIDesignTokens.BrandColors.Primary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xxs))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(IWBIDesignTokens.space_m))
                trailingContent()
            }
        }
    }
}

