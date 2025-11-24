package com.mobile.iwbi.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mobile.iwbi.domain.store.Store
import com.mobile.iwbi.domain.store.StoreDataProvider
import com.mobile.iwbi.presentation.components.IWBIBottomBar
import com.mobile.iwbi.presentation.components.friends.AddFriendPanel
import com.mobile.iwbi.presentation.components.friends.FriendRequestsPanel
import com.mobile.iwbi.presentation.components.friends.FriendsPanel
import com.mobile.iwbi.presentation.components.friends.ShareNotePanel
import com.mobile.iwbi.presentation.components.login.LoginScreen
import com.mobile.iwbi.presentation.components.profile.ProfilePanel
import com.mobile.iwbi.presentation.components.shoppingnotes.ShoppingNotesPanel
import com.mobile.iwbi.presentation.components.store.StoreMapScreen
import com.mobile.iwbi.presentation.components.store.StorePanel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.typeOf

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = koinViewModel<AppViewModel>()
    val state by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    if (currentUser == null) {
        LoginScreen()
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
                // Home screen now uses Improved Enhanced Shopping Notes
                composable<Panel.HomePanel> {
                    ShoppingNotesPanel(
                        onNavigateToShareNote = { noteId ->
                            navController.navigate(Panel.ShareNotePanel(noteId))
                        },
                        onCreateNewNote = {
                            // This will be handled by the panel's internal ViewModel
                        },
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

                // Friends management main screen
                composable<Panel.FriendsPanel> {
                    FriendsPanel(
                        onNavigateToAddFriend = {
                            navController.navigate(Panel.AddFriendPanel)
                        },
                        onNavigateToFriendRequests = {
                            navController.navigate(Panel.FriendRequestsPanel)
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

                // Friend management screens
                composable<Panel.AddFriendPanel> {
                    AddFriendPanel(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable<Panel.FriendRequestsPanel> {
                    FriendRequestsPanel(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable<Panel.ShareNotePanel> { backStackEntry ->
                    val args = backStackEntry.toRoute<Panel.ShareNotePanel>()
                    ShareNotePanel(
                        noteId = args.noteId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
