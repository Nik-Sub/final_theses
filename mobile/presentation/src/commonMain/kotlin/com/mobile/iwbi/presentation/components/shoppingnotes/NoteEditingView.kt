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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iwbi.domain.shopping.ShoppingNote

@Composable
fun ImprovedNoteEditingView(
    note: ShoppingNote,
    newItemText: String,
    onNewItemTextChange: (String) -> Unit,
    onNoteTitleChange: (String) -> Unit,
    onSaveTitle: () -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String, Int) -> Unit,
    onToggleItem: (String, Int) -> Unit,
    onDeleteNote: (String) -> Unit,
    onSaveAsTemplate: (String) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingTitle by remember { mutableStateOf(false) }
    var titleText by remember(note.title) { mutableStateOf(note.title) }
    var showDeleteNoteDialog by remember { mutableStateOf(false) }

    // Update title text when note changes (real-time updates)
    LaunchedEffect(note.title) {
        if (!isEditingTitle) {
            titleText = note.title
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button, editable title, and delete note button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            if (isEditingTitle) {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onNoteTitleChange(titleText)
                            onSaveTitle()
                            isEditingTitle = false
                        }
                    )
                )
                IconButton(
                    onClick = {
                        onNoteTitleChange(titleText)
                        onSaveTitle()
                        isEditingTitle = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save title",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            titleText = note.title
                            isEditingTitle = true
                        }
                )
                IconButton(
                    onClick = {
                        titleText = note.title
                        isEditingTitle = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit title",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Save as template button
                IconButton(
                    onClick = { onSaveAsTemplate(note.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Save as template",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Delete note button with warning styling
                IconButton(
                    onClick = { showDeleteNoteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entire note",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (!isEditingTitle) {
            Text(
                text = "Tap title to edit • Bookmark for template • Trash to delete",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newItemText.trim().isNotEmpty()) {
                                onAddItem()
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = onAddItem,
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

            if (note.items.isNotEmpty()) {
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
                text = "Tap on any item to mark it as completed • Tap delete for smooth removal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Items list with animations for add/remove
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(
                items = note.items,
                key = { index, item -> "${item.name}_$index" }
            ) { index, item ->
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
                        onToggle = { onToggleItem(note.id, index) },
                        onRemove = { onRemoveItem(note.id, index) }
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
