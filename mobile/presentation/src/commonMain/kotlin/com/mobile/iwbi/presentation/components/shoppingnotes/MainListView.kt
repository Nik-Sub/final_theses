package com.mobile.iwbi.presentation.components.shoppingnotes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.iwbi.domain.shopping.ShoppingNote
import com.mobile.iwbi.presentation.uistate.ShoppingNotesUiState

@Composable
fun ImprovedMainListView(
    uiState: ShoppingNotesUiState,
    onCreateNote: (String, List<String>) -> Unit,
    onSelectNote: (String) -> Unit,
    onToggleItem: (String, Int) -> Unit,
    onDeleteNote: (String) -> Unit,
    onShareNote: (String) -> Unit = {},
    onSaveAsTemplate: (String) -> Unit = {},
    isTemplateAlreadyExists: (String) -> Boolean = { false },
    onCategoryChange: (NoteCategory) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // State to track which notes are expanded
    var expandedNotes by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with create button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Shopping Lists",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
                if (uiState.templates.isNotEmpty()) {
                    Text(
                        "${uiState.templates.size} templates available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = { onCreateNote("New Shopping List", emptyList()) }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category tabs
        NoteCategoriesHeader(
            selectedCategory = uiState.selectedCategory,
            onCategoryChange = onCategoryChange,
            myNotesCount = uiState.myNotes.size,
            sharedNotesCount = uiState.sharedNotes.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Notes list based on selected category
            val notesToShow = when (uiState.selectedCategory) {
                NoteCategory.MY_NOTES -> uiState.myNotes
                NoteCategory.SHARED_WITH_ME -> uiState.sharedNotes
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notesToShow) { note ->
                    ExpandableShoppingNoteCard(
                        note = note,
                        isOwner = uiState.currentUserId == note.createdBy,
                        isExpanded = expandedNotes.contains(note.id),
                        onExpandToggle = {
                            expandedNotes = if (expandedNotes.contains(note.id)) {
                                expandedNotes - note.id
                            } else {
                                expandedNotes + note.id
                            }
                        },
                        onNoteClick = { onSelectNote(note.id) },
                        onItemToggle = onToggleItem,
                        onDeleteNote = { onDeleteNote(note.id) },
                        onShareNote = { onShareNote(note.id) },
                        onSaveAsTemplate = { onSaveAsTemplate(note.id) },
                        isTemplateAlreadyExists = isTemplateAlreadyExists
                    )
                }

                if (notesToShow.isEmpty()) {
                    item {
                        EmptyNotesState(
                            message = when (uiState.selectedCategory) {
                                NoteCategory.MY_NOTES -> "No shopping lists yet"
                                NoteCategory.SHARED_WITH_ME -> "No shared lists yet"
                            },
                            onCreateNote = { onCreateNote("New Shopping List", emptyList()) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableShoppingNoteCard(
    note: ShoppingNote,
    isOwner: Boolean,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onNoteClick: (ShoppingNote) -> Unit,
    onItemToggle: (String, Int) -> Unit,
    onDeleteNote: (ShoppingNote) -> Unit,
    onShareNote: (ShoppingNote) -> Unit,
    onSaveAsTemplate: (ShoppingNote) -> Unit = {},
    isTemplateAlreadyExists: (String) -> Boolean = { false }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // FIX 1: Make whole header clickable for expansion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandToggle() }, // Entire header is now clickable
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${note.items.size} items • ${note.items.count { it.isChecked }} completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Action buttons
                Row {
                    // Save as template button (only show if template doesn't already exist)
                    if (!isTemplateAlreadyExists(note.title)) {
                        IconButton(onClick = { onSaveAsTemplate(note) }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Save as template",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    if (isOwner) {
                        IconButton(onClick = { onShareNote(note) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share note",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = { onDeleteNote(note) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete note",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Show arrow as visual indicator (header is clickable, not just this icon)
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )

                    IconButton(onClick = { onNoteClick(note) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit note",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // FIX 2: Expandable content with bigger text and green background for checked items
            if (isExpanded && note.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                note.items.take(5).forEachIndexed { index, item ->
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium, // Made text bigger (was bodyMedium)
                        color = if (item.isChecked) {
                            Color.White // White text on green background for better contrast
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                        fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (item.isChecked) {
                                    Color.Green.copy(alpha = 0.8f) // Green background when checked
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onItemToggle(note.id, index) }
                            .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 12.dp) // Increased padding for better touch target
                    )
                }

                if (note.items.size > 5) {
                    Text(
                        text = "... and ${note.items.size - 5} more items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteCategoriesHeader(
    selectedCategory: NoteCategory,
    onCategoryChange: (NoteCategory) -> Unit,
    myNotesCount: Int,
    sharedNotesCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategory == NoteCategory.MY_NOTES,
            onClick = { onCategoryChange(NoteCategory.MY_NOTES) },
            label = { Text("My Notes ($myNotesCount)") }
        )

        FilterChip(
            selected = selectedCategory == NoteCategory.SHARED_WITH_ME,
            onClick = { onCategoryChange(NoteCategory.SHARED_WITH_ME) },
            label = { Text("Shared ($sharedNotesCount)") }
        )
    }
}

@Composable
fun EmptyNotesState(
    message: String,
    onCreateNote: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first shopping list to get started!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCreateNote) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create List")
        }
    }
}


// Placeholder for UI state and enums that would be defined elsewhere
enum class NoteCategory {
    MY_NOTES,
    SHARED_WITH_ME
}
