package com.mobile.iwbi.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mobile.iwbi.domain.store.Store
import com.mobile.iwbi.domain.store.StoreDataProvider
import com.mobile.iwbi.presentation.components.IWBIBottomBar
import com.mobile.iwbi.presentation.components.home.HomePanelContent
import com.mobile.iwbi.presentation.components.login.LoginScreen
import com.mobile.iwbi.presentation.components.profile.ProfilePanel
import com.mobile.iwbi.presentation.components.store.StoreMapScreen
import com.mobile.iwbi.presentation.components.store.StorePanel
import kotlin.reflect.typeOf

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry

    val isLoginScreen = currentDestination?.destination?.route == Panel.LoginPanel::class.qualifiedName

    if (isLoginScreen) {
        // Show login screen without scaffold
        LoginScreen(
            onLogin = {
                navController.navigate(Panel.HomePanel) {
                    popUpTo(Panel.LoginPanel) { inclusive = true }
                }
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                IWBIBottomBar(
                    isSelected = { mainScreen ->
                        mainScreen.screen == state.currentMainScreen
                    },
                    onClick = { mainPanel: MainPanel ->
                        val panel = mainPanel.screen
                        navController.navigate(panel) {
                            popUpTo(state.currentMainScreen) {
                                saveState = true
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        viewModel.updateMainScreen(panel)
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Panel.HomePanel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = paddingValues.calculateBottomPadding(),
                        top = paddingValues.calculateTopPadding()
                    )
            ) {
                composable<Panel.HomePanel> {
                    HomePanelContent(
                        modifier = Modifier.padding(0.dp)
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
                composable<Panel.ProfilePanel> {
                    ProfilePanel(
                        modifier = Modifier.padding(0.dp)
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
                        routeToItem = "Route to ${args.store}"
                    )
                }
            }
        }
    }
}
