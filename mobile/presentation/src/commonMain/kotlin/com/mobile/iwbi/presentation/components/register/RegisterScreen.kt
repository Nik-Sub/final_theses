package com.mobile.iwbi.presentation.components.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardPadding
import com.mobile.iwbi.presentation.design.components.IWBIButton
import com.mobile.iwbi.presentation.design.components.IWBIButtonStyle
import com.mobile.iwbi.presentation.design.components.IWBIButtonSize
import com.mobile.iwbi.presentation.design.components.IWBICard
import com.mobile.iwbi.presentation.design.components.IWBICardStyle
import com.mobile.iwbi.presentation.design.components.IWBITextField
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    val viewModel = koinViewModel<RegisterPanelViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IWBICard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(StandardPadding),
            style = IWBICardStyle.ELEVATED,
            padding = IWBIDesignTokens.space_xl
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_l)
            ) {
                // App branding
                Text(
                    text = "IWBI",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = IWBIDesignTokens.BrandColors.Primary
                )

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

                // Email field with branded styling
                IWBITextField(
                    value = uiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = "Email",
                    placeholder = "Enter your email",
                    leadingIcon = Icons.Default.Email,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                // Password field with branded styling and visibility toggle inside
                IWBITextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Default.Lock,
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    trailingContent = {
                        TextButton(
                            onClick = { isPasswordVisible = !isPasswordVisible }
                        ) {
                            Text(
                                text = if (isPasswordVisible) "Hide" else "Show",
                                style = MaterialTheme.typography.labelMedium,
                                color = IWBIDesignTokens.BrandColors.Primary
                            )
                        }
                    }
                )

                // Confirm Password field
                IWBITextField(
                    value = uiState.confirmPassword,
                    onValueChange = { viewModel.updateConfirmPassword(it) },
                    label = "Confirm Password",
                    placeholder = "Re-enter your password",
                    leadingIcon = Icons.Default.Lock,
                    singleLine = true,
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    trailingContent = {
                        TextButton(
                            onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                        ) {
                            Text(
                                text = if (isConfirmPasswordVisible) "Hide" else "Show",
                                style = MaterialTheme.typography.labelMedium,
                                color = IWBIDesignTokens.BrandColors.Primary
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

                // Register button
                IWBIButton(
                    text = "Create Account",
                    onClick = { viewModel.onRegisterClick() },
                    modifier = Modifier.fillMaxWidth(),
                    style = IWBIButtonStyle.PRIMARY,
                    size = IWBIButtonSize.LARGE,
                    enabled = uiState.email.isNotBlank() &&
                              uiState.password.isNotBlank() &&
                              uiState.confirmPassword.isNotBlank() &&
                              uiState.password == uiState.confirmPassword
                )

                // Already have an account
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = onNavigateToLogin
                    ) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = IWBIDesignTokens.BrandColors.Primary
                        )
                    }
                }
            }
        }
    }
}

