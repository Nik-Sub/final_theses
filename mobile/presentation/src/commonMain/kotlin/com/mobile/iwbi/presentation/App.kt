package com.mobile.iwbi.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobile.iwbi.presentation.home.HomePanel
import com.mobile.iwbi.presentation.login.LoginScreen
import com.mobile.iwbi.presentation.navigation.Panel

@Composable
fun App() {
    val navController = rememberNavController()
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = Panel.LoginPanel
        ) {
            composable<Panel.LoginPanel> {
                LoginScreen(
                    onLogin = {
                        navController.navigate(Panel.HomePanel)
                    }
                )
            }
            composable<Panel.HomePanel> {
                HomePanel()
            }
        }
    }
}
