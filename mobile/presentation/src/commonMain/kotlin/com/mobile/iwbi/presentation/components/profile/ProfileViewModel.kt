package com.mobile.iwbi.presentation.components.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.domain.auth.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authenticationService: AuthenticationServicePort
) : ViewModel() {

    val currentUser: StateFlow<User?> = authenticationService.observeCurrentUser()

    fun signOut() {
        viewModelScope.launch {
            authenticationService.signOut()
        }
    }
}
