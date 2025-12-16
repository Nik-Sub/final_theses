package com.mobile.iwbi.presentation.components.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.presentation.uistate.RegisterPanelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterPanelViewModel(
    private val authenticationServicePort: AuthenticationServicePort
): ViewModel() {
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            if (_uiState.value.password != _uiState.value.confirmPassword) {
                println("NIKOLA: Passwords don't match")
                return@launch
            }

            val result = authenticationServicePort.signUpWithEmailPassword(
                _uiState.value.email,
                _uiState.value.password
            )
            println("NIKOLA: ${result}")
        }
    }

    private val _uiState = MutableStateFlow(RegisterPanelUiState())
    val uiState: StateFlow<RegisterPanelUiState> = _uiState.asStateFlow()
}

