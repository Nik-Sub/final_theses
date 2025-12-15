package com.mobile.iwbi.presentation.design.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mobile.iwbi.presentation.design.IWBIDesignTokens

/**
 * IWBI Standardized Screen Headers
 *
 * Consistent header components for all screens
 */

@Composable
fun IWBIScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xxs))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_xs),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))
    }
}

@Composable
fun IWBISimpleHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xxs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))
    }
}

@Composable
fun IWBISectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null
) {
    val displayTitle = if (count != null) "$title ($count)" else title

    Text(
        text = displayTitle,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

