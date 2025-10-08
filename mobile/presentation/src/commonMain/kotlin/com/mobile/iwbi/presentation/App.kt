package com.mobile.iwbi.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mobile.iwbi.domain.store.Store
import com.mobile.iwbi.domain.store.StoreDataProvider
import com.mobile.iwbi.presentation.home.HomePanel
import com.mobile.iwbi.presentation.login.LoginScreen
import com.mobile.iwbi.presentation.navigation.CustomNavType
import com.mobile.iwbi.presentation.navigation.Panel
import com.mobile.iwbi.presentation.store.StoreMapScreen
import com.mobile.iwbi.presentation.store.StorePanel
import kotlin.reflect.typeOf

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
                HomePanel(
                    onHomeIconClick = { /* Handle home icon click */ },
                    onStoreIconClick = { navController.navigate(Panel.StorePanel) },
                    onProfileIconClick = { /* Handle profile icon click */ }
                )
            }
            composable<Panel.StorePanel> {
                StorePanel(
                    stores = StoreDataProvider.getSampleStores(),
                    onStoreSelected = { store ->
                        navController.navigate(Panel.StoreMapScreen(store))
                    }
                )
            }
            composable<Panel.StoreMapScreen>(
                typeMap = mapOf(
                    typeOf<Store>() to CustomNavType.storeNavType
                )
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<Panel.StoreMapScreen>()

                StoreMapScreen(
                    store = args.store,
                    onSearchItem = { /* ... */ },
                    routeToItem = "Route to ${args.store}",
                )
            }
        }
    }
}
