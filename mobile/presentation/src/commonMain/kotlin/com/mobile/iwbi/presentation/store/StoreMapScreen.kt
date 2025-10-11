package com.mobile.iwbi.presentation.store

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.iwbi.domain.map.StoreLayout
import com.mobile.iwbi.domain.map.MapSection
import com.mobile.iwbi.domain.map.NodeType
import com.mobile.iwbi.domain.map.PathfindingService
import com.mobile.iwbi.domain.store.Store
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreMapScreen(
    store: Store,
    onSearchItem: (String) -> Unit,
    routeToItem: String? = null
) {
    var searchText by remember { mutableStateOf(routeToItem ?: "") }
    var selectedSection by remember { mutableStateOf<MapSection?>(null) }
    var highlightedPath by remember { mutableStateOf<List<MapSection>>(emptyList()) }

    val storeLayout = remember(store.mapOfTheStore) {
        try {
            Json.decodeFromString<StoreLayout>(store.mapOfTheStore)
        } catch (e: Exception) {
            null
        }
    }

    val pathfindingService = remember { PathfindingService() }

    // Create a compact square layout with sections only (no corridor tiles)
    val enhancedLayout = remember(storeLayout) {
        storeLayout?.let { layout ->
            // Create a more compact, square-like arrangement
            val sections = layout.sections.filter { it.type != NodeType.PATHWAY }
            val gridSize = kotlin.math.ceil(kotlin.math.sqrt(sections.size.toDouble())).toInt()

            // Rearrange sections in a square grid, leaving space for corridors
            val rearrangedSections = sections.mapIndexed { index, section ->
                val row = (index / gridSize) * 2 // Multiply by 2 to leave space for corridors
                val col = (index % gridSize) * 2 // Multiply by 2 to leave space for corridors
                section.copy(x = col, y = row, width = 1, height = 1)
            }

            val expandedGridSize = gridSize * 2 - 1 // Account for corridors

            layout.copy(
                sections = rearrangedSections, // Only sections, no corridor tiles
                gridWidth = expandedGridSize,
                gridHeight = expandedGridSize
            )
        }
    }

    // Handle search and highlighting
    LaunchedEffect(searchText, storeLayout) {
        if (searchText.isNotEmpty() && storeLayout != null) {
            val pathResult = pathfindingService.findNearestProductLocation(storeLayout, searchText)
            if (pathResult != null) {
                selectedSection = pathResult.targetSection
                // Find the rearranged section
                val compactSection = enhancedLayout?.sections?.find { it.id == pathResult.targetSection.id }
                val entrance = enhancedLayout?.sections?.find { it.type == NodeType.ENTRANCE }

                if (compactSection != null && entrance != null) {
                    highlightedPath = pathfindingService.generatePath(enhancedLayout!!, entrance, compactSection)
                } else {
                    highlightedPath = listOfNotNull(compactSection)
                }
                onSearchItem(searchText)
            }
        } else {
            selectedSection = null
            highlightedPath = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Map of ${store.name}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Modern Search Bar
            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { /* handled by LaunchedEffect */ },
                active = false,
                onActiveChange = { },
                placeholder = { Text("Search for products (e.g. milk, bread, apples)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = if (searchText.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                } else null,
                modifier = Modifier.fillMaxWidth()
            ) { }

            Spacer(modifier = Modifier.height(16.dp))

            if (enhancedLayout != null) {
                // Store Map with square aspect ratio
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f), // Force square shape
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        // Canvas for drawing paths as lines
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawPaths(enhancedLayout, highlightedPath, this)
                        }

                        // Overlay sections on top of paths
                        CompactStoreFloorPlan(
                            layout = enhancedLayout,
                            selectedSection = selectedSection,
                            highlightedPath = highlightedPath,
                            onSectionClick = { section ->
                                selectedSection = if (selectedSection == section) null else section
                                if (selectedSection != null) {
                                    val entrance = enhancedLayout.sections.find { it.type == NodeType.ENTRANCE }
                                    if (entrance != null) {
                                        highlightedPath = pathfindingService.generatePath(enhancedLayout, entrance, section)
                                    } else {
                                        highlightedPath = listOf(section)
                                    }
                                } else {
                                    highlightedPath = emptyList()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Search result or selection information
                AnimatedVisibility(
                    visible = selectedSection != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    selectedSection?.let { section ->
                        Spacer(modifier = Modifier.height(12.dp))
                        SectionInfoCard(
                            section = section,
                            searchTerm = searchText,
                            onDismiss = {
                                selectedSection = null
                                highlightedPath = emptyList()
                            }
                        )
                    }
                }
            } else {
                // Error state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Unable to load store map",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Please try again later",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

fun drawPaths(
    layout: StoreLayout,
    highlightedPath: List<MapSection>,
    drawScope: DrawScope
) {
    val cellWidth = drawScope.size.width / layout.gridWidth
    val cellHeight = drawScope.size.height / layout.gridHeight

    with(drawScope) {
        // Draw corridor grid lines
        val corridorColor = Color.Gray.copy(alpha = 0.4f)
        val lineWidth = 2.dp.toPx()

        // Draw horizontal corridor lines (between rows of sections)
        for (row in 1 until layout.gridHeight step 2) {
            val y = (row * cellHeight) + (cellHeight / 2)
            drawLine(
                color = corridorColor,
                start = Offset(0f, y),
                end = Offset(drawScope.size.width, y),
                strokeWidth = lineWidth
            )
        }

        // Draw vertical corridor lines (between columns of sections)
        for (col in 1 until layout.gridWidth step 2) {
            val x = (col * cellWidth) + (cellWidth / 2)
            drawLine(
                color = corridorColor,
                start = Offset(x, 0f),
                end = Offset(x, drawScope.size.height),
                strokeWidth = lineWidth
            )
        }

        // Draw highlighted path as single connected bright blue line
        if (highlightedPath.size >= 2) {
            val pathColor = Color(0xFF2196F3)
            val highlightedPathDrawable = Path()

            // Start the path from the first section
            val firstSection = highlightedPath.first()
            val startX = (firstSection.x * cellWidth) + (cellWidth / 2)
            val startY = (firstSection.y * cellHeight) + (cellHeight / 2)
            highlightedPathDrawable.moveTo(startX, startY)

            // Create one continuous connected path through all sections
            for (i in 1 until highlightedPath.size) {
                val current = highlightedPath[i-1]
                val next = highlightedPath[i]

                val currentX = (current.x * cellWidth) + (cellWidth / 2)
                val currentY = (current.y * cellHeight) + (cellHeight / 2)
                val nextX = (next.x * cellWidth) + (cellWidth / 2)
                val nextY = (next.y * cellHeight) + (cellHeight / 2)

                // Connect with L-shaped path (horizontal first, then vertical)
                highlightedPathDrawable.lineTo(nextX, currentY) // Horizontal line
                highlightedPathDrawable.lineTo(nextX, nextY)   // Vertical line
            }

            // Draw the entire path as one connected line
            drawPath(
                path = highlightedPathDrawable,
                color = pathColor,
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw arrow at the final destination
            val lastSection = highlightedPath.last()
            val endX = (lastSection.x * cellWidth) + (cellWidth / 2)
            val endY = (lastSection.y * cellHeight) + (cellHeight / 2)

            val arrowSize = 8.dp.toPx()
            val arrowPath = Path().apply {
                moveTo(endX - arrowSize, endY - arrowSize)
                lineTo(endX, endY)
                lineTo(endX - arrowSize, endY + arrowSize)
            }
            drawPath(
                path = arrowPath,
                color = pathColor,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

@Composable
fun CompactStoreFloorPlan(
    layout: StoreLayout,
    selectedSection: MapSection?,
    highlightedPath: List<MapSection>,
    onSectionClick: (MapSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(layout.gridHeight) { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                repeat(layout.gridWidth) { col ->
                    val section = layout.sections.find { it.x == col && it.y == row }

                    if (section != null) {
                        SectionTile(
                            section = section,
                            isSelected = selectedSection == section,
                            isHighlighted = highlightedPath.contains(section),
                            onClick = { onSectionClick(section) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                        )
                    } else {
                        // Invisible spacer to maintain grid structure
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTile(
    section: MapSection,
    isSelected: Boolean,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val pulseAnimation = rememberInfiniteTransition()
    val pulseAlpha by pulseAnimation.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = modifier
            .scale(animatedScale)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) {
                Color.parseColor(section.color).copy(alpha = pulseAlpha)
            } else {
                Color.parseColor(section.color)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Icon(
                imageVector = getIconForNodeType(section.type),
                contentDescription = section.name,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Emoji if available
            if (section.emoji.isNotEmpty()) {
                Text(
                    text = section.emoji,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Name
            Text(
                text = section.name,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PathwayTile(
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    }

    Box(
        modifier = modifier
            .background(
                backgroundColor,
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isHighlighted) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Path",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        } else {
            // Show subtle pathway indicators for normal paths
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun EmptySpaceTile(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.background,
                RoundedCornerShape(4.dp)
            )
    )
}

@Composable
fun SectionInfoCard(
    section: MapSection,
    searchTerm: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (searchTerm.isNotEmpty()) {
                    Text(
                        "Found '$searchTerm' in:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (section.emoji.isNotEmpty()) {
                        Text(
                            text = section.emoji,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        section.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                if (section.products.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Products: ${section.products.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun getIconForNodeType(type: NodeType): ImageVector {
    return when (type) {
        NodeType.ENTRANCE -> Icons.Default.Home
        NodeType.SECTION -> Icons.Default.ShoppingCart
        NodeType.CHECKOUT -> Icons.Default.ShoppingCart
        NodeType.RESTROOM -> Icons.Default.ShoppingCart
        NodeType.CUSTOMER_SERVICE -> Icons.Default.Info
        NodeType.PATHWAY -> Icons.Default.ShoppingCart
    }
}

// Extension function to parse color strings - Multiplatform compatible
private fun Color.Companion.parseColor(colorString: String): Color {
    return try {
        if (colorString.startsWith("#")) {
            val hex = colorString.substring(1)
            when (hex.length) {
                6 -> {
                    val r = hex.substring(0, 2).toInt(16) / 255f
                    val g = hex.substring(2, 4).toInt(16) / 255f
                    val b = hex.substring(4, 6).toInt(16) / 255f
                    Color(r, g, b)
                }
                8 -> {
                    val a = hex.substring(0, 2).toInt(16) / 255f
                    val r = hex.substring(2, 4).toInt(16) / 255f
                    val g = hex.substring(4, 6).toInt(16) / 255f
                    val b = hex.substring(6, 8).toInt(16) / 255f
                    Color(r, g, b, a)
                }
                else -> Color.Gray
            }
        } else {
            Color.Gray
        }
    } catch (e: Exception) {
        Color.Gray
    }
}
