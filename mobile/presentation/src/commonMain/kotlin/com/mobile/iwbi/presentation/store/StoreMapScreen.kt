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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.iwbi.domain.map.MapData
import com.mobile.iwbi.domain.map.MapNode
import com.mobile.iwbi.domain.map.NodeType
import com.mobile.iwbi.domain.map.PathfindingService
import com.mobile.iwbi.domain.store.Store
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreMapScreen(
    store: Store,
    onSearchItem: (String) -> Unit,
    routeToItem: String?
) {
    var searchText by remember { mutableStateOf("") }
    var currentPath by remember { mutableStateOf<List<String>>(emptyList()) }
    var userLocation by remember { mutableStateOf("entrance") }

    val mapData = remember(store.mapOfTheStore) {
        try {
            Json.decodeFromString<MapData>(store.mapOfTheStore)
        } catch (e: Exception) {
            null
        }
    }

    val pathfindingService = remember { PathfindingService() }

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

    // Handle search and pathfinding
    LaunchedEffect(searchText, mapData, userLocation) {
        if (searchText.isNotEmpty() && mapData != null) {
            val pathResult = pathfindingService.findNearestProductLocation(
                mapData, userLocation, searchText
            )
            currentPath = pathResult?.path ?: emptyList()
            if (pathResult != null) {
                onSearchItem(searchText)
            }
        } else {
            currentPath = emptyList()
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

            // Current location selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your location: ", style = MaterialTheme.typography.bodyMedium)

                if (mapData != null) {
                    val locationNodes = mapData.nodes.filter {
                        it.type == NodeType.ENTRANCE || it.type == NodeType.PATHWAY
                    }

                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = mapData.nodes.find { it.id == userLocation }?.label ?: userLocation,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            locationNodes.forEach { node ->
                                DropdownMenuItem(
                                    text = { Text(node.label ?: node.id) },
                                    onClick = {
                                        userLocation = node.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (mapData != null) {
                // Map Canvas taking remaining space
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // This makes the Canvas take all remaining space
                        .clip(MaterialTheme.shapes.medium),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawStoreMap(mapData, textMeasurer, currentPath, userLocation)
                        }
                    }
                }

                // Path information
                if (currentPath.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Route found for '$searchText':",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            val pathLabels = currentPath.mapNotNull { nodeId ->
                                mapData.nodes.find { it.id == nodeId }?.label?.takeIf { it.isNotEmpty() }
                            }
                            if (pathLabels.isNotEmpty()) {
                                Text(
                                    pathLabels.joinToString(" → "),
                                    style = MaterialTheme.typography.bodyMedium
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

private fun DrawScope.drawStoreMap(
    mapData: MapData,
    textMeasurer: TextMeasurer,
    currentPath: List<String>,
    userLocation: String
) {
    // Calculate bounds of the map data
    val minX = mapData.nodes.minOfOrNull { it.x - it.width/2 } ?: 0f
    val maxX = mapData.nodes.maxOfOrNull { it.x + it.width/2 } ?: 0f
    val minY = mapData.nodes.minOfOrNull { it.y - it.height/2 } ?: 0f
    val maxY = mapData.nodes.maxOfOrNull { it.y + it.height/2 } ?: 0f

    val mapWidth = maxX - minX
    val mapHeight = maxY - minY

    // Add padding around the map (10% on each side)
    val padding = 0.1f
    val paddedWidth = mapWidth * (1 + 2 * padding)
    val paddedHeight = mapHeight * (1 + 2 * padding)

    // Calculate scale to fit the Canvas while maintaining aspect ratio
    val scaleX = size.width / paddedWidth
    val scaleY = size.height / paddedHeight
    val scale = minOf(scaleX, scaleY)

    // Calculate offsets to center the map
    val scaledMapWidth = mapWidth * scale
    val scaledMapHeight = mapHeight * scale
    val offsetX = (size.width - scaledMapWidth) / 2 - (minX * scale)
    val offsetY = (size.height - scaledMapHeight) / 2 - (minY * scale)

    // Helper function to transform coordinates
    fun transformX(x: Float) = x * scale + offsetX
    fun transformY(y: Float) = y * scale + offsetY
    fun transformSize(size: Float) = size * scale

    // Draw pathway grid/floor
    drawRect(
        color = Color(0xFFEEEEEE),
        size = size
    )

    // Draw pathways as lighter areas
    mapData.nodes.filter { it.type == NodeType.PATHWAY }.forEach { node ->
        drawRoundRect(
            color = Color(0xFFDDDDDD),
            topLeft = Offset(
                transformX(node.x - node.width/2),
                transformY(node.y - node.height/2)
            ),
            size = Size(transformSize(node.width), transformSize(node.height)),
            cornerRadius = CornerRadius(8f * scale, 8f * scale)
        )
    }

    // Draw edges (pathways between nodes)
    currentPath.zipWithNext().forEach { (fromId, toId) ->
        val fromNode = mapData.nodes.find { it.id == fromId }
        val toNode = mapData.nodes.find { it.id == toId }
        if (fromNode != null && toNode != null) {
            drawLine(
                color = Color(0xFF4CAF50),
                start = Offset(transformX(fromNode.x), transformY(fromNode.y)),
                end = Offset(transformX(toNode.x), transformY(toNode.y)),
                strokeWidth = 8f * scale
            )
        }
    }

    // Draw regular connections
    mapData.edges.forEach { edge ->
        val fromNode = mapData.nodes.find { it.id == edge.from }
        val toNode = mapData.nodes.find { it.id == edge.to }
        if (fromNode != null && toNode != null &&
            !currentPath.contains(edge.from) && !currentPath.contains(edge.to)) {
            drawLine(
                color = Color(0xFFBDBDBD),
                start = Offset(transformX(fromNode.x), transformY(fromNode.y)),
                end = Offset(transformX(toNode.x), transformY(toNode.y)),
                strokeWidth = 2f * scale
            )
        }
    }

    // Draw nodes based on their type
    mapData.nodes.forEach { node ->
        when (node.type) {
            NodeType.ENTRANCE -> drawEntrance(node, textMeasurer, node.id == userLocation, scale, ::transformX, ::transformY, ::transformSize)
            NodeType.SHELF -> drawShelf(node, textMeasurer, currentPath.contains(node.id), scale, ::transformX, ::transformY, ::transformSize)
            NodeType.CHECKOUT -> drawCheckout(node, textMeasurer, scale, ::transformX, ::transformY, ::transformSize)
            NodeType.RESTROOM -> drawRestroom(node, textMeasurer, scale, ::transformX, ::transformY, ::transformSize)
            NodeType.CUSTOMER_SERVICE -> drawCustomerService(node, textMeasurer, scale, ::transformX, ::transformY, ::transformSize)
            NodeType.EXIT -> drawExit(node, textMeasurer, scale, ::transformX, ::transformY, ::transformSize)
            NodeType.PATHWAY -> {} // Already drawn above
        }
    }
}

private fun DrawScope.drawEntrance(
    node: MapNode,
    textMeasurer: TextMeasurer,
    isUserLocation: Boolean,
    scale: Float,
    transformX: (Float) -> Float,
    transformY: (Float) -> Float,
    transformSize: (Float) -> Float
) {
    val color = if (isUserLocation) Color(0xFF2196F3) else Color(0xFF4CAF50)

    // Draw entrance door shape
    val path = Path().apply {
        val left = transformX(node.x - node.width/2)
        val right = transformX(node.x + node.width/2)
        val top = transformY(node.y - node.height/2)
        val bottom = transformY(node.y + node.height/2)

        moveTo(left, bottom)
        lineTo(left, top)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(left, top, right, bottom),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        lineTo(right, bottom)
        close()
    }

    drawPath(
        path = path,
        color = color
    )

    if (isUserLocation) {
        drawCircle(
            color = Color.White,
            radius = 8f * scale,
            center = Offset(transformX(node.x), transformY(node.y))
        )
    }

    node.label?.let { label ->
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(
                transformX(node.x) - 30f * scale,
                transformY(node.y + node.height/2) + 5f * scale
            ),
            style = TextStyle(
                fontSize = (10 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        )
    }
}

private fun DrawScope.drawShelf(
    node: MapNode,
    textMeasurer: TextMeasurer,
    isHighlighted: Boolean,
    scale: Float,
    transformX: (Float) -> Float,
    transformY: (Float) -> Float,
    transformSize: (Float) -> Float
) {
    val color = if (isHighlighted) Color(0xFFFF9800) else Color(0xFF8D6E63)

    // Draw shelf rectangle
    drawRoundRect(
        color = color,
        topLeft = Offset(
            transformX(node.x - node.width/2),
            transformY(node.y - node.height/2)
        ),
        size = Size(transformSize(node.width), transformSize(node.height)),
        cornerRadius = CornerRadius(4f * scale, 4f * scale)
    )

    // Draw shelf details (lines to represent shelves)
    for (i in 1..2) {
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(
                transformX(node.x - node.width/2) + 4f * scale,
                transformY(node.y - node.height/2) + i * transformSize(node.height)/3
            ),
            end = Offset(
                transformX(node.x + node.width/2) - 4f * scale,
                transformY(node.y - node.height/2) + i * transformSize(node.height)/3
            ),
            strokeWidth = 1f * scale
        )
    }

    node.label?.let { label ->
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(
                transformX(node.x - node.width/2),
                transformY(node.y + node.height/2) + 5f * scale
            ),
            style = TextStyle(
                fontSize = (9 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        )
    }
}

private fun DrawScope.drawCheckout(
    node: MapNode,
    textMeasurer: TextMeasurer,
    scale: Float,
    transformX: (Float) -> Float,
    transformY: (Float) -> Float,
    transformSize: (Float) -> Float
) {
    // Draw checkout counter
    drawRoundRect(
        color = Color(0xFF607D8B),
        topLeft = Offset(
            transformX(node.x - node.width/2),
            transformY(node.y - node.height/2)
        ),
        size = Size(transformSize(node.width), transformSize(node.height)),
        cornerRadius = CornerRadius(6f * scale, 6f * scale)
    )

    // Draw conveyor belt
    drawRect(
        color = Color(0xFF37474F),
        topLeft = Offset(
            transformX(node.x - node.width/2) + 4f * scale,
            transformY(node.y) - 2f * scale
        ),
        size = Size(transformSize(node.width) - 8f * scale, 4f * scale)
    )

    node.label?.let { label ->
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(
                transformX(node.x) - 25f * scale,
                transformY(node.y + node.height/2) + 5f * scale
            ),
            style = TextStyle(
                fontSize = (9 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        )
    }
}

private fun DrawScope.drawRestroom(
    node: MapNode,
    textMeasurer: TextMeasurer,
    scale: Float,
    transformX: (Float) -> Float,
    transformY: (Float) -> Float,
    transformSize: (Float) -> Float
) {
    drawRoundRect(
        color = Color(0xFF9C27B0),
        topLeft = Offset(
            transformX(node.x - node.width/2),
            transformY(node.y - node.height/2)
        ),
        size = Size(transformSize(node.width), transformSize(node.height)),
        cornerRadius = CornerRadius(4f * scale, 4f * scale)
    )

    // Draw door
    drawRect(
        color = Color.White.copy(alpha = 0.3f),
        topLeft = Offset(
            transformX(node.x) - 3f * scale,
            transformY(node.y - node.height/2)
        ),
        size = Size(6f * scale, transformSize(node.height))
    )

    node.label?.let { label ->
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(
                transformX(node.x) - 20f * scale,
                transformY(node.y + node.height/2) + 5f * scale
            ),
            style = TextStyle(
                fontSize = (9 * scale).sp,
                color = Color(0xFF333333)
            )
        )
    }
}

private fun DrawScope.drawCustomerService(
    node: MapNode,
    textMeasurer: TextMeasurer,
    scale: Float,
    transformX: (Float) -> Float,
    transformY: (Float) -> Float,
    transformSize: (Float) -> Float
) {
    drawRoundRect(
        color = Color(0xFF3F51B5),
        topLeft = Offset(
            transformX(node.x - node.width/2),
            transformY(node.y - node.height/2)
        ),
        size = Size(transformSize(node.width), transformSize(node.height)),
        cornerRadius = CornerRadius(4f * scale, 4f * scale)
    )

    // Draw desk
    drawRect(
        color = Color.White.copy(alpha = 0.2f),
        topLeft = Offset(
            transformX(node.x - node.width/2) + 4f * scale,
            transformY(node.y) - 2f * scale
        ),
        size = Size(transformSize(node.width) - 8f * scale, 4f * scale)
    )

    node.label?.let { label ->
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(
                transformX(node.x) - 35f * scale,
                transformY(node.y + node.height/2) + 5f * scale
            ),
            style = TextStyle(
                fontSize = (9 * scale).sp,
                color = Color(0xFF333333)
            )
        )
    }
}

private fun DrawScope.drawExit(
    node: MapNode,
    textMeasurer: TextMeasurer,
    scale: Float,
    transformX: (Float) -> Float,
    transformY: (Float) -> Float,
    transformSize: (Float) -> Float
) {
    drawRoundRect(
        color = Color(0xFFF44336),
        topLeft = Offset(
            transformX(node.x - node.width/2),
            transformY(node.y - node.height/2)
        ),
        size = Size(transformSize(node.width), transformSize(node.height)),
        cornerRadius = CornerRadius(4f * scale, 4f * scale),
        style = Stroke(width = 3f * scale)
    )

    node.label?.let { label ->
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(
                transformX(node.x) - 15f * scale,
                transformY(node.y + node.height/2) + 5f * scale
            ),
            style = TextStyle(
                fontSize = (9 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF44336)
            )
        )
    }
}
