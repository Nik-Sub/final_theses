package com.mobile.iwbi.domain.map

import kotlinx.serialization.Serializable

@Serializable
enum class NodeType {
    ENTRANCE,
    SECTION,
    CHECKOUT,
    RESTROOM,
    PATHWAY,
    CUSTOMER_SERVICE
}

@Serializable
data class MapSection(
    val id: String,
    val name: String,
    val emoji: String = "",
    val x: Int, // Grid column
    val y: Int, // Grid row
    val width: Int = 1, // Grid cells wide
    val height: Int = 1, // Grid cells tall
    val type: NodeType = NodeType.SECTION,
    val color: String = "#8D6E63", // Hex color
    val products: List<String> = emptyList()
)

@Serializable
data class StoreLayout(
    val gridWidth: Int = 6, // Number of columns
    val gridHeight: Int = 8, // Number of rows
    val sections: List<MapSection>,
    val productLocations: Map<String, List<String>> = emptyMap()
)
