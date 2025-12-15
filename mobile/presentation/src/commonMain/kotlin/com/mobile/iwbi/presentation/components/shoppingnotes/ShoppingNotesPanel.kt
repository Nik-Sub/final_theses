package com.mobile.iwbi.presentation.components.shoppingnotes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingNotesPanel(
    onNavigateToShareNote: (String) -> Unit,
    onCreateNewNote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ShoppingNotesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for template bottom sheet
    var showTemplateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Show error messages via snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            // Friend sharing view
            uiState.isSharing && uiState.selectedNoteForSharing != null -> {
                FriendSharingView(
                    note = uiState.selectedNoteForSharing!!,
                    friends = uiState.friends,
                    selectedFriends = uiState.selectedFriends,
                    searchQuery = uiState.friendSearchQuery,
                    onSearchQueryChange = viewModel::updateFriendSearchQuery,
                    onToggleFriend = viewModel::toggleFriendSelection,
                    onRemoveFriend = viewModel::removeFriendFromNote,
                    onShareWithSelected = viewModel::shareWithSelectedFriends,
                    onCancel = viewModel::cancelSharing
                )
            }

            // Note editing view
            uiState.isEditingNote && uiState.selectedNote != null -> {
                ImprovedNoteEditingView(
                    note = uiState.selectedNote!!,
                    newItemText = uiState.newItemText,
                    onNewItemTextChange = viewModel::updateNewItemText,
                    onNoteTitleChange = viewModel::updateNoteTitleText,
                    onSaveTitle = viewModel::saveNoteTitle,
                    onAddItem = viewModel::addItemToSelectedNote,
                    onRemoveItem = viewModel::removeItemFromNote,
                    onToggleItem = viewModel::toggleItem,
                    onDeleteNote = viewModel::deleteNote,
                    onSaveAsTemplate = viewModel::saveNoteAsTemplate,
                    isTemplateAlreadyExists = viewModel::isTemplateAlreadyExists,
                    onBack = viewModel::exitEditMode,
                    modifier = modifier
                )
            }

            // Main list view
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ImprovedMainListView(
                        uiState = uiState,
                        onCreateNote = { title, sharedWith -> viewModel.createNewNote(title, sharedWith) },
                        onSelectNote = viewModel::selectNote,
                        onToggleItem = viewModel::toggleItem,
                        onDeleteNote = viewModel::deleteNote,
                        onShareNote = viewModel::startSharing,
                        onSaveAsTemplate = viewModel::saveNoteAsTemplate,
                        isTemplateAlreadyExists = viewModel::isTemplateAlreadyExists,
                        onCategoryChange = viewModel::selectCategory,
                        modifier = modifier
                    )

                    // Floating Action Button for Templates
                    if (uiState.selectedCategory == NoteCategory.MY_NOTES && uiState.templates.isNotEmpty()) {
                        ExtendedFloatingActionButton(
                            onClick = { showTemplateSheet = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Quick Templates"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Templates (${uiState.templates.size})")
                        }
                    }
                }
            }
        }

        // Template Bottom Sheet
        if (showTemplateSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        showTemplateSheet = false
                    }
                },
                sheetState = sheetState
            ) {
                QuickTemplateSelection(
                    templates = uiState.templates,
                    onTemplateSelected = { template ->
                        viewModel.createNoteFromTemplate(template)
                        scope.launch {
                            showTemplateSheet = false
                        }
                    },
                    onTemplateRemoved = { template ->
                        viewModel.removeTemplate(template)
                    },
                    onDismiss = {
                        scope.launch {
                            showTemplateSheet = false
                        }
                    }
                )
            }
        }

        // Snackbar for error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
