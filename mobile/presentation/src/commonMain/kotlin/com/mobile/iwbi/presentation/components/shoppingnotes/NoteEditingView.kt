package com.mobile.iwbi.presentation.components.shoppingnotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iwbi.domain.shopping.ShoppingNote
import com.mobile.iwbi.presentation.components.IWBITopAppBar
import com.mobile.iwbi.presentation.design.IWBIDesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedNoteEditingView(
    note: ShoppingNote,
    isCreatingNewNote: Boolean = false,
    newItemText: String,
    onNewItemTextChange: (String) -> Unit,
    onNoteTitleChange: (String) -> Unit,
    onSaveTitle: () -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String, Int) -> Unit,
    onToggleItem: (String, Int) -> Unit,
    onDeleteNote: (String) -> Unit,
    onShareNote: (String) -> Unit = {},
    onSaveAsTemplate: (String) -> Unit = {},
    onSaveNewNote: () -> Unit = {},
    onDiscardNewNote: () -> Unit = {},
    isTemplateAlreadyExists: (String) -> Boolean = { false },
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingTitle by remember { mutableStateOf(false) }
    var titleText by remember(note.title) { mutableStateOf(note.title) }
    var showDeleteNoteDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Update title text when note changes (real-time updates)
    LaunchedEffect(note.title) {
        if (!isEditingTitle) {
            titleText = note.title
        }
    }

    Scaffold(
        topBar = {
            IWBITopAppBar(
                headerTitle = note.title,
                onLeadingIconClick = if (isCreatingNewNote) onDiscardNewNote else onBack,
                actions = {
                    // Edit title button (for both new and existing notes)
                    IconButton(
                        onClick = {
                            titleText = note.title
                            isEditingTitle = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit title"
                        )
                    }

                    // Share button (for both new and existing notes)
                    if (isCreatingNewNote) {
                        // For new notes, share will be available after saving
                        IconButton(
                            onClick = { /* Will be enabled after save */ },
                            enabled = false
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share note (save first)",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        }
                    } else {
                        IconButton(onClick = { onShareNote(note.id) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share note",
                                tint = IWBIDesignTokens.BrandColors.Primary
                            )
                        }
                    }

                    // Save as template button (for both new and existing notes)
                    if (!isTemplateAlreadyExists(note.title)) {
                        if (isCreatingNewNote) {
                            // For new notes, template will be available after saving
                            IconButton(
                                onClick = { /* Will be enabled after save */ },
                                enabled = false
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Save as template (save first)",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            }
                        } else {
                            IconButton(onClick = { onSaveAsTemplate(note.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Save as template"
                                )
                            }
                        }
                    }

                    // Save/Delete button (Save for new notes, Delete for existing notes)
                    if (isCreatingNewNote) {
                        // Save button for new notes
                        IconButton(
                            onClick = onSaveNewNote,
                            enabled = note.title.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Save note",
                                tint = IWBIDesignTokens.BrandColors.Success
                            )
                        }
                    } else {
                        // Delete note button (only for existing notes)
                        IconButton(onClick = { showDeleteNoteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete note",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(IWBIDesignTokens.space_m)
        ) {
            // Title editing field (if in edit mode)
            if (isEditingTitle) {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Note Title") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onNoteTitleChange(titleText)
                                onSaveTitle()
                                isEditingTitle = false
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save title",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onNoteTitleChange(titleText)
                            onSaveTitle()
                            isEditingTitle = false
                            focusManager.clearFocus()
                        }
                    )
                )
                Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))
            }

            // Add item section with animation for new items
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically() + fadeIn()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newItemText,
                        onValueChange = onNewItemTextChange,
                        placeholder = { Text("Enter new item...") },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (newItemText.trim().isNotEmpty()) {
                                    onAddItem()
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(
                        onClick = {
                            onAddItem()
                            focusManager.clearFocus()
                        },
                        enabled = newItemText.trim().isNotEmpty()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items list header with real-time count updates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Items (${note.items.size})",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.titleLarge
                )

                if (note.items.isNotEmpty() && !isCreatingNewNote) {
                    val completedItems = note.items.count { it.isChecked }
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn()
                    ) {
                        Text(
                            text = "$completedItems of ${note.items.size} completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (note.items.isNotEmpty()) {
                Text(
                    text = if (isCreatingNewNote) {
                        "Tap delete to remove items • Save the note to mark items as completed"
                    } else {
                        "Tap on any item to mark it as completed • Tap delete for smooth removal"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Items list with animations for add/remove
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m),
                contentPadding = PaddingValues(vertical = IWBIDesignTokens.space_m)
            ) {
                // Sort items: unchecked first, then checked
                val sortedItems = note.items.sortedBy { it.isChecked }

                itemsIndexed(
                    items = sortedItems,
                    key = { index, item -> "${item.name}_${item.isChecked}_$index" }
                ) { sortedIndex, item ->
                    // Find the original index for proper toggle/remove operations
                    val originalIndex = note.items.indexOf(item)

                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn(
                            animationSpec = tween(300)
                        ) + expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ),
                        exit = slideOutVertically(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = androidx.compose.animation.core.FastOutSlowInEasing
                            )
                        ) + fadeOut(
                            animationSpec = tween(200)
                        ) + shrinkVertically(
                            animationSpec = tween(300)
                        )
                    ) {
                        UltraSmoothShoppingListItem(
                            item = item,
                            onToggle = { onToggleItem(note.id, originalIndex) },
                            onRemove = { onRemoveItem(note.id, originalIndex) },
                            enableToggle = !isCreatingNewNote
                        )
                    }
                }

                if (note.items.isEmpty()) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + scaleIn()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No items yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Add your first item above!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete note confirmation dialog
    if (showDeleteNoteDialog) {
        DeleteNoteConfirmationDialog(
            noteTitle = note.title,
            onConfirm = {
                onDeleteNote(note.id)
                onBack() // Go back after deletion
            },
            onDismiss = { showDeleteNoteDialog = false }
        )
    }
}
