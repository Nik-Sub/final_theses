package com.mobile.iwbi.presentation.components.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.presentation.uistate.LoginPanelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginPanelViewModel(
    private val authenticationServicePort: AuthenticationServicePort
): ViewModel() {
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onLoginClick() {
        viewModelScope.launch {
            val result = authenticationServicePort.signInWithEmailPassword(_uiState.value.email, _uiState.value.password)
            println("NIKOLA: ${result}")
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            val result = authenticationServicePort.signUpWithEmailPassword(_uiState.value.email, _uiState.value.password)
            println("NIKOLA: ${result}")
        }
    }

    private val _uiState = MutableStateFlow(LoginPanelUiState())
    val uiState: StateFlow<LoginPanelUiState> = _uiState.asStateFlow()
}