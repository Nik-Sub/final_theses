package com.mobile.iwbi.presentation

import androidx.compose.runtime.Composable
import com.mobile.iwbi.presentation.login.LoginScreen

class Greeting {
    private val platform: Platform = getPlatform()

    @Composable
    fun greet() {
        //return "Hello, ${platform.name}!"
        LoginScreen()
    }
}