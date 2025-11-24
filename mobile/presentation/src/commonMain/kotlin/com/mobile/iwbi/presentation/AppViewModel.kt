package com.mobile.iwbi.presentation

import androidx.lifecycle.ViewModel
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppUiState(
    val currentMainScreen: Panel = Panel.HomePanel,
    val previousMainScreen: Panel = Panel.HomePanel
)

class AppViewModel(
    private val authenticationService: AuthenticationServicePort
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    val currentUser = authenticationService.observeCurrentUser()

    fun updateMainScreen(panel: Panel) {
        _uiState.value = _uiState.value.copy(
            previousMainScreen = _uiState.value.currentMainScreen,
            currentMainScreen = panel
        )
    }
}

