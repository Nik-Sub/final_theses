package com.mobile.iwbi.presentation.components.shoppingnotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iwbi.domain.shopping.ShoppingItem
import kotlinx.coroutines.delay

@Composable
fun UltraSmoothShoppingListItem(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    var isDeleting by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    // Handle the delete animation sequence
    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(150) // Short delay for smooth animation
            onRemove()
        }
    }

    AnimatedVisibility(
        visible = !isDeleting,
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutSlowInEasing
            )
        ) + scaleOut(
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(
                    animateFloatAsState(
                        targetValue = if (showConfirmDelete) 0.98f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    ).value
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (showConfirmDelete) {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                } else if (item.isChecked) {
                    Color.Green.copy(alpha = 0.8f) // Green background when checked
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (showConfirmDelete) 8.dp else 4.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item text - clickable to toggle
                Text(
                    text = item.name,
                    fontSize = 20.sp,
                    fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
                    color = if (item.isChecked) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    modifier = Modifier
                        .weight(1f),
                    lineHeight = 24.sp
                )

                // Button area - fixed size container for perfect alignment
                Box(
                    modifier = Modifier.size(width = 144.dp, height = 48.dp)
                ) {
                    // Normal buttons (check + delete) - fade out smoothly
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !showConfirmDelete,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ),
                        exit = fadeOut(
                            animationSpec = tween(150)
                        ) + scaleOut(
                            animationSpec = tween(150)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Delete button
                            IconButton(
                                onClick = {
                                    println("Delete button clicked for item: ${item.name}")
                                    showConfirmDelete = true
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete item",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Confirmation buttons (cancel + confirm) - fade in at exact same position
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showConfirmDelete,
                        enter = fadeIn(
                            animationSpec = tween(250)
                        ) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ),
                        exit = fadeOut(
                            animationSpec = tween(200)
                        ) + scaleOut(
                            targetScale = 0.9f,
                            animationSpec = tween(200)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cancel button
                            IconButton(
                                onClick = { showConfirmDelete = false },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cancel delete",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Confirm delete button
                            IconButton(
                                onClick = { isDeleting = true },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Confirm delete",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
