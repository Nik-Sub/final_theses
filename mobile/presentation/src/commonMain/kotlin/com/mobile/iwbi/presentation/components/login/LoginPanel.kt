package com.mobile.iwbi.presentation.components.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
fun LoginScreen() {
    val viewModel = koinViewModel<LoginPanelViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    var isPasswordVisible by remember { mutableStateOf(false) }

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
                    text = "Welcome Back",
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

                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

                // Login button
                IWBIButton(
                    text = "Login",
                    onClick = { viewModel.onLoginClick() },
                    modifier = Modifier.fillMaxWidth(),
                    style = IWBIButtonStyle.PRIMARY,
                    size = IWBIButtonSize.LARGE,
                    enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank()
                )

                // Register button
                IWBIButton(
                    text = "Create Account",
                    onClick = { viewModel.onRegisterClick() },
                    modifier = Modifier.fillMaxWidth(),
                    style = IWBIButtonStyle.OUTLINED,
                    size = IWBIButtonSize.LARGE
                )
            }
        }
    }
}