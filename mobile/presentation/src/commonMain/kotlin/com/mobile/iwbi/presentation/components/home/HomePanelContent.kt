package com.mobile.iwbi.presentation.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.mobile.iwbi.presentation.components.layout.IWBIButton
import com.mobile.iwbi.presentation.components.layout.IWBICard
import com.mobile.iwbi.presentation.components.layout.IWBIContentContainer
import com.mobile.iwbi.presentation.components.layout.IWBIEmptyState
import com.mobile.iwbi.presentation.design.IWBIDesignTokens
import com.mobile.iwbi.presentation.design.StandardCornerRadius
import com.mobile.iwbi.presentation.uistate.HomePanelUiState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomePanelContent(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<HomePanelViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isEditingNote && uiState.selectedNote != null) {
        // Show editing view when a note is selected
        NoteEditingView(
            note = uiState.selectedNote!!,
            newItemText = uiState.newItemText,
            onNewItemTextChange = viewModel::updateNewItemText,
            onAddItem = viewModel::addItemToSelectedNote,
            onRemoveItem = viewModel::removeItemFromNote,
            onBack = viewModel::exitEditMode,
            modifier = modifier
        )
    } else {
        // Show main list view
        MainListView(
            uiState = uiState,
            onCreateNote = viewModel::createNewNote,
            onSelectNote = viewModel::selectNote,
            onToggleItem = viewModel::toggleItem,
            onDeleteNote = viewModel::deleteNote,
            onCreateFromTemplate = viewModel::createNoteFromTemplate,
            modifier = modifier
        )
    }
}

@Composable
fun NoteEditingView(
    note: ShoppingNote,
    newItemText: String,
    onNewItemTextChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    IWBIContentContainer(
        modifier = modifier
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        // Add item section
        IWBICard {
            Text(
                text = "Add New Item",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
            ) {
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = onNewItemTextChange,
                    placeholder = { Text("Enter new item...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(StandardCornerRadius)
                )
                IWBIButton(
                    text = "Add",
                    onClick = onAddItem,
                    enabled = newItemText.trim().isNotEmpty(),
                    icon = Icons.Default.Add
                )
            }
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        // Items list header
        Text(
            text = "Items (${note.items.size})",
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

        // Items list
        if (note.items.isEmpty()) {
            IWBIEmptyState(
                icon = Icons.Default.ShoppingCart,
                title = "No items yet",
                subtitle = "Add your first item above!"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
            ) {
                items(note.items.size) { index ->
                    EditableShoppingListItem(
                        item = note.items[index],
                        onRemove = { onRemoveItem(note.id, index) }
                    )
                }
            }
        }
    }
}

@Composable
fun EditableShoppingListItem(
    item: ShoppingItem,
    onRemove: () -> Unit
) {
    IWBICard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
        ) {
            Text(
                text = "• ${item.name}",
                fontWeight = if (item.isChecked) FontWeight.Light else FontWeight.Normal,
                color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun MainListView(
    uiState: HomePanelUiState,
    onCreateNote: (String, List<String>) -> Unit,
    onSelectNote: (String) -> Unit,
    onToggleItem: (String, Int) -> Unit,
    onDeleteNote: (String) -> Unit,
    onCreateFromTemplate: (List<ShoppingItem>, String) -> Unit,
    modifier: Modifier = Modifier
) {
    IWBIContentContainer(
        modifier = modifier
    ) {
        // Header with create button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shopping Notes",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            IWBIButton(
                text = "New Note",
                onClick = { onCreateNote("New Shopping List", emptyList()) },
                icon = Icons.Default.Add
            )
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_l))

        if (uiState.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (uiState.shoppingNotes.isEmpty()) {
            IWBIEmptyState(
                icon = Icons.Default.ShoppingCart,
                title = "No shopping notes yet",
                subtitle = "Create your first shopping list to get started!",
                action = {
                    IWBIButton(
                        text = "Create First Note",
                        onClick = { onCreateNote("My First Shopping List", emptyList()) },
                        icon = Icons.Default.Add
                    )
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
            ) {
                items(uiState.shoppingNotes.size) { index ->
                    ShoppingNoteCard(
                        note = uiState.shoppingNotes[index],
                        onNoteClick = { onSelectNote(it.id) },
                        onItemToggle = onToggleItem,
                        onDeleteNote = { onDeleteNote(it.id) }
                    )
                }
            }
        }

        // Templates section
        if (uiState.templates.isNotEmpty()) {
            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xl))

            Text(
                text = "Templates",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_s)
            ) {
                items(uiState.templates.size) { index ->
                    TemplateItem(
                        template = uiState.templates[index],
                        onClick = {
                            onCreateFromTemplate(
                                uiState.templates[index],
                                "From Template ${index + 1}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingNoteCard(
    note: ShoppingNote,
    onNoteClick: (ShoppingNote) -> Unit,
    onItemToggle: (String, Int) -> Unit,
    onDeleteNote: (ShoppingNote) -> Unit
) {
    IWBICard(
        onClick = { onNoteClick(note) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = { onDeleteNote(note) }
            ) {
                Text(
                    text = "Delete",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_s))

        Text(
            text = "Created by: ${note.createdBy}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (note.sharedWith.isNotEmpty()) {
            Spacer(modifier = Modifier.height(IWBIDesignTokens.space_xs))
            Text(
                text = "Shared with: ${note.sharedWith.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(IWBIDesignTokens.space_m))

        if (note.items.isEmpty()) {
            Text(
                text = "No items yet - Click to add items!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        } else {
            note.items.forEachIndexed { index, item ->
                ShoppingListItem(
                    item = item,
                    onClick = { onItemToggle(note.id, index) }
                )
            }
        }
    }
}

@Composable
fun ShoppingListItem(item: ShoppingItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = IWBIDesignTokens.space_xs)
            .background(
                if (item.isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(IWBIDesignTokens.corner_radius_s)
            )
            .clickable { onClick() }
            .padding(IWBIDesignTokens.space_s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "• ${item.name}",
            fontWeight = if (item.isChecked) FontWeight.Light else FontWeight.Normal,
            color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TemplateItem(template: List<ShoppingItem>, onClick: () -> Unit) {
    IWBICard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IWBIDesignTokens.space_m)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(IWBIDesignTokens.icon_size_default)
            )

            Text(
                text = "Template: ${template.joinToString(", ") { it.name }}",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
