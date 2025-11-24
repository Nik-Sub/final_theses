package com.mobile.iwbi.presentation.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add item section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = onNewItemTextChange,
                placeholder = { Text("Enter new item...") },
                modifier = Modifier.weight(1f)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Items list
        Text(
            text = "Items (${note.items.size})",
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(note.items.size) { index ->
                EditableShoppingListItem(
                    item = note.items[index],
                    onRemove = { onRemoveItem(note.id, index) }
                )
            }

            if (note.items.isEmpty()) {
                item {
                    Text(
                        text = "No items yet. Add your first item above!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "• ${item.name}",
                fontWeight = if (item.isChecked) FontWeight.Light else FontWeight.Normal,
                color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Shopping Notes",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = { onCreateNote("New Shopping List", emptyList()) }
            ) {
                Text("+ New Note")
            }
        }

        if (uiState.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Templates",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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

@Composable
fun ShoppingNoteCard(
    note: ShoppingNote,
    onNoteClick: (ShoppingNote) -> Unit,
    onItemToggle: (String, Int) -> Unit,
    onDeleteNote: (ShoppingNote) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNoteClick(note) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

                TextButton(
                    onClick = { onDeleteNote(note) }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }

            Text(
                text = "Created by: ${note.createdBy}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (note.sharedWith.isNotEmpty()) {
                Text(
                    text = "Shared with: ${note.sharedWith.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            note.items.forEachIndexed { index, item ->
                ShoppingListItem(
                    item = item,
                    onClick = { onItemToggle(note.id, index) }
                )
            }

            if (note.items.isEmpty()) {
                Text(
                    text = "No items yet - Click to add items!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
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
            .padding(vertical = 4.dp)
            .background(
                if (item.isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "• ${item.name}",
            fontWeight = if (item.isChecked) FontWeight.Light else FontWeight.Normal,
            color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
        )
    }
}

@Composable
fun TemplateItem(template: List<ShoppingItem>, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋 Template: ${template.joinToString(", ") { it.name }}",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
