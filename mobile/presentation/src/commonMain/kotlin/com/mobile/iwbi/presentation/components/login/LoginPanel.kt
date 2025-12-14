package com.mobile.iwbi.presentation.components.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun LoginScreen() {
    val viewModel = koinViewModel<LoginPanelViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(StandardPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xl))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val icon = if (isPasswordVisible) "Hide" else "Show"
                TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Text(icon)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
        ) {
            Button(
                onClick = { viewModel.onLoginClick() },
                modifier = Modifier.weight(1f).height(IWBIDesignTokens.button_height)
            ) {
                Text("Login")
            }
            Button(
                onClick = { viewModel.onRegisterClick() },
                modifier = Modifier.weight(1f).height(IWBIDesignTokens.button_height)
            ) {
                Text("Register")
            }
        }
    }
}