package com.mobile.iwbi.presentation.design.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardCornerRadius

/**
 * IWBI Branded Input Components
 */

@Composable
fun IWBITextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    errorText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isError) {
                    IWBIDesignTokens.BrandColors.Error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(bottom = IWBIDesignTokens.space_xs)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = if (placeholder != null) {
                { Text(placeholder, style = MaterialTheme.typography.bodyMedium) }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(IWBIDesignTokens.IconSizes.Default)
                    )
                }
            } else null,
            trailingIcon = if (trailingIcon != null) {
                {
                    IconButton(
                        onClick = { onTrailingIconClick?.invoke() },
                        enabled = onTrailingIconClick != null
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            modifier = Modifier.size(IWBIDesignTokens.IconSizes.Default)
                        )
                    }
                }
            } else null,
            supportingText = if (isError && errorText != null) {
                { Text(errorText) }
            } else if (supportingText != null) {
                { Text(supportingText) }
            } else null,
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(StandardCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IWBIDesignTokens.BrandColors.Primary,
                unfocusedBorderColor = IWBIDesignTokens.ContentColors.Outline,
                errorBorderColor = IWBIDesignTokens.BrandColors.Error,
                focusedLabelColor = IWBIDesignTokens.BrandColors.Primary,
                cursorColor = IWBIDesignTokens.BrandColors.Primary
            )
        )
    }
}

@Composable
fun IWBIFilledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    errorText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier) {
        if (label != null && !singleLine) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isError) {
                    IWBIDesignTokens.BrandColors.Error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(bottom = IWBIDesignTokens.space_xs)
            )
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = if (label != null && singleLine) {
                { Text(label) }
            } else null,
            placeholder = if (placeholder != null) {
                { Text(placeholder, style = MaterialTheme.typography.bodyMedium) }
            } else null,
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(IWBIDesignTokens.IconSizes.Default)
                    )
                }
            } else null,
            trailingIcon = if (trailingIcon != null) {
                {
                    IconButton(
                        onClick = { onTrailingIconClick?.invoke() },
                        enabled = onTrailingIconClick != null
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            modifier = Modifier.size(IWBIDesignTokens.IconSizes.Default)
                        )
                    }
                }
            } else null,
            supportingText = if (isError && errorText != null) {
                { Text(errorText) }
            } else if (supportingText != null) {
                { Text(supportingText) }
            } else null,
            isError = isError,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(StandardCornerRadius),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = IWBIDesignTokens.BrandColors.Primary,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = IWBIDesignTokens.BrandColors.Error,
                focusedLabelColor = IWBIDesignTokens.BrandColors.Primary,
                cursorColor = IWBIDesignTokens.BrandColors.Primary,
                focusedContainerColor = IWBIDesignTokens.BrandColors.PrimaryContainer.copy(alpha = 0.3f),
                unfocusedContainerColor = IWBIDesignTokens.SurfaceColors.SurfaceVariant
            )
        )
    }
}

@Composable
fun IWBISearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onClear: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    IWBIFilledTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Search,
        trailingIcon = if (value.isNotEmpty()) Icons.Default.Clear else null,
        onTrailingIconClick = onClear,
        enabled = enabled,
        singleLine = true
    )
}

@Composable
fun IWBITextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    errorText: String? = null,
    enabled: Boolean = true,
    minLines: Int = 3,
    maxLines: Int = 6,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    IWBITextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        isError = isError,
        errorText = errorText,
        enabled = enabled,
        singleLine = false,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

