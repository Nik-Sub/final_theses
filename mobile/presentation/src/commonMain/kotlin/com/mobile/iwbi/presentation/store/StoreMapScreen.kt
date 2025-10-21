package com.mobile.iwbi.presentation.store

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.iwbi.domain.map.StoreLayout
import com.mobile.iwbi.domain.map.MapSection
import com.mobile.iwbi.domain.map.NodeType
import com.mobile.iwbi.domain.map.PathfindingService
import com.mobile.iwbi.domain.map.PathfindingService.PathNode
import com.mobile.iwbi.domain.store.Store
import kotlinx.serialization.json.Json
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreMapScreen(
    store: Store,
    onSearchItem: (String) -> Unit,
    routeToItem: String? = null
) {
    var searchText by remember { mutableStateOf("") }
    var highlightedSection by remember { mutableStateOf<MapSection?>(null) }
    var pathToDisplay by remember { mutableStateOf<List<PathNode>>(emptyList()) }

    val storeLayout = remember(store.mapOfTheStore) {
        try {
            Json.decodeFromString<StoreLayout>(store.mapOfTheStore)
        } catch (e: Exception) {
            null
        }
    }

    val pathfindingService = remember { PathfindingService() }

    // Handle search and highlighting
    LaunchedEffect(searchText, storeLayout) {
        if (searchText.isNotEmpty() && storeLayout != null) {
            val pathResult = pathfindingService.findNearestProductLocation(storeLayout, searchText)
            highlightedSection = pathResult?.targetSection
            pathToDisplay = pathResult?.path ?: emptyList()
            if (pathResult != null) {
                onSearchItem(searchText)
            }
        } else {
            highlightedSection = null
            pathToDisplay = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map of ${store.name}") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    "Search for products (e.g., chocolate, bread, milk)",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (storeLayout != null) {
                // Store Map taking remaining space
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(MaterialTheme.shapes.medium),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                            .padding(16.dp)
                    ) {
                        StoreFloorPlan(
                            layout = storeLayout,
                            highlightedSection = highlightedSection,
                            pathToDisplay = pathToDisplay,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Search result information
                highlightedSection?.let { section ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Found '$searchText' in:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${section.emoji} ${section.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            if (pathToDisplay.isNotEmpty()) {
                                Text(
                                    "🔷 Path highlighted from entrance (${pathToDisplay.size} steps)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Invalid map data",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoreFloorPlan(
    layout: StoreLayout,
    highlightedSection: MapSection?,
    pathToDisplay: List<PathNode> = emptyList(),
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LayoutDirection.Ltr

    val textMeasurer = remember {
        TextMeasurer(
            defaultDensity = density,
            defaultFontFamilyResolver = fontFamilyResolver,
            defaultLayoutDirection = layoutDirection
        )
    }

    Canvas(modifier = modifier) {
        val cellSize = minOf(size.width / layout.gridWidth, size.height / layout.gridHeight)
        val totalWidth = layout.gridWidth * cellSize
        val totalHeight = layout.gridHeight * cellSize
        val offsetX = (size.width - totalWidth) / 2
        val offsetY = (size.height - totalHeight) / 2

        translate(offsetX, offsetY) {
            // Draw grid background
            drawGridBackground(layout, cellSize)

            // Draw path visualization BEFORE sections so sections appear on top
            if (pathToDisplay.isNotEmpty()) {
                drawPath(pathToDisplay, cellSize)
            }

            // Draw all sections
            layout.sections.forEach { section ->
                val isHighlighted = section.id == highlightedSection?.id
                drawSection(section, cellSize, isHighlighted, textMeasurer)
            }
        }
    }
}

private fun DrawScope.drawGridBackground(layout: StoreLayout, cellSize: Float) {
    // Draw light grid lines
    val gridColor = Color.Gray.copy(alpha = 0.1f)

    // Vertical lines
    for (x in 0..layout.gridWidth) {
        drawLine(
            color = gridColor,
            start = Offset(x * cellSize, 0f),
            end = Offset(x * cellSize, layout.gridHeight * cellSize),
            strokeWidth = 1.dp.toPx()
        )
    }

    // Horizontal lines
    for (y in 0..layout.gridHeight) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y * cellSize),
            end = Offset(layout.gridWidth * cellSize, y * cellSize),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawPath(pathNodes: List<PathNode>, cellSize: Float) {
    if (pathNodes.isEmpty()) return

    val pathColor = Color(0xFF2196F3) // Blue color for path
    val pathWidth = 4.dp.toPx()

    // Draw path lines
    for (i in 0 until pathNodes.size - 1) {
        val current = pathNodes[i]
        val next = pathNodes[i + 1]

        val startX = (current.x * cellSize) + cellSize / 2
        val startY = (current.y * cellSize) + cellSize / 2
        val endX = (next.x * cellSize) + cellSize / 2
        val endY = (next.y * cellSize) + cellSize / 2

        drawLine(
            color = pathColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = pathWidth
        )
    }

    // Draw path nodes as small circles
    pathNodes.forEach { node ->
        val centerX = (node.x * cellSize) + cellSize / 2
        val centerY = (node.y * cellSize) + cellSize / 2

        drawCircle(
            color = pathColor,
            radius = 3.dp.toPx(),
            center = Offset(centerX, centerY)
        )
    }

    // Draw start marker (entrance)
    if (pathNodes.isNotEmpty()) {
        val startNode = pathNodes.first()
        val startX = (startNode.x * cellSize) + cellSize / 2
        val startY = (startNode.y * cellSize) + cellSize / 2

        drawCircle(
            color = Color(0xFF4CAF50), // Green for start
            radius = 6.dp.toPx(),
            center = Offset(startX, startY)
        )
    }

    // Draw end marker (target)
    if (pathNodes.size > 1) {
        val endNode = pathNodes.last()
        val endX = (endNode.x * cellSize) + cellSize / 2
        val endY = (endNode.y * cellSize) + cellSize / 2

        drawCircle(
            color = Color(0xFFFF5722), // Red-orange for target
            radius = 6.dp.toPx(),
            center = Offset(endX, endY)
        )
    }
}

private fun DrawScope.drawSection(
    section: MapSection,
    cellSize: Float,
    isHighlighted: Boolean,
    textMeasurer: TextMeasurer
) {
    val x = section.x * cellSize
    val y = section.y * cellSize
    val width = section.width * cellSize
    val height = section.height * cellSize

    // Parse color from hex string
    val baseColor = try {
        val colorInt = section.color.removePrefix("#").toLong(16).toInt() or 0xFF000000.toInt()
        Color(colorInt)
    } catch (e: Exception) {
        when (section.type) {
            NodeType.ENTRANCE -> Color(0xFF4CAF50)
            NodeType.PATHWAY -> Color(0xFFEEEEEE)
            NodeType.SECTION -> Color(0xFF2196F3)
            NodeType.CHECKOUT -> Color(0xFFFF9800)
            NodeType.RESTROOM -> Color(0xFF9C27B0)
            NodeType.CUSTOMER_SERVICE -> Color(0xFF607D8B)
        }
    }

    val sectionColor = if (isHighlighted) {
        baseColor.copy(alpha = 1f)
    } else {
        baseColor.copy(alpha = 0.8f)
    }

    // Draw section background
    drawRoundRect(
        color = sectionColor,
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = CornerRadius(4.dp.toPx())
    )

    // Draw border
    val borderColor = if (isHighlighted) Color.Black else Color.Gray
    val borderWidth = if (isHighlighted) 3.dp.toPx() else 1.dp.toPx()

    drawRoundRect(
        color = borderColor,
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = CornerRadius(4.dp.toPx()),
        style = Stroke(width = borderWidth)
    )

    // Draw section text
    if (section.type != NodeType.PATHWAY) {
        val fontSize = min(cellSize * 0.15f, 12f)
        val textStyle = TextStyle(
            fontSize = fontSize.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        val text = "${section.emoji}\n${section.name}"
        val textLayoutResult = textMeasurer.measure(text, textStyle)

        val textX = x + (width - textLayoutResult.size.width) / 2
        val textY = y + (height - textLayoutResult.size.height) / 2

        drawText(
            textMeasurer = textMeasurer,
            text = text,
            topLeft = Offset(textX, textY),
            style = textStyle
        )
    }
}
