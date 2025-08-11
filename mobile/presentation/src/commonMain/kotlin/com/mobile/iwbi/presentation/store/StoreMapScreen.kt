package com.mobile.iwbi.presentation.store

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.mobile.iwbi.domain.map.MapData
import com.mobile.iwbi.domain.store.Store
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreMapScreen(
    store: Store,
    onSearchItem: (String) -> Unit,
    routeToItem: String?
) {
    val mapData = remember(store.mapOfTheStore) {
        try {
            Json.decodeFromString<MapData>(store.mapOfTheStore)
        } catch (e: Exception) {
            null // Handle invalid JSON
        }
    }

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

    val typography = MaterialTheme.typography.bodyMedium

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Map of ${store.name}") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (mapData != null) {
                Text("Store Map:", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw edges
                        mapData.edges.forEach { edge ->
                            val fromNode = mapData.nodes.find { it.id == edge.from }
                            val toNode = mapData.nodes.find { it.id == edge.to }
                            if (fromNode != null && toNode != null) {
                                drawLine(
                                    color = Color.Red,
                                    start = Offset(fromNode.x, fromNode.y),
                                    end = Offset(toNode.x, toNode.y),
                                    strokeWidth = 4f
                                )
                            }
                        }

                        // Draw nodes
                        mapData.nodes.forEach { node ->
                            if (node.label != null) {
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = node.label!!, // Ensure this is a String
                                    topLeft = Offset(node.x, node.y - 15),
                                    style = typography,
                                    overflow = TextOverflow.Clip,
                                    softWrap = true,
                                    maxLines = Int.MAX_VALUE
                                )
                            }
                        }
                    }
                }
            } else {
                Text("Invalid map data", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}