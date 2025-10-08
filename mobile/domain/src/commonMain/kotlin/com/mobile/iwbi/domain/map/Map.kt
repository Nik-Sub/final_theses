package com.mobile.iwbi.domain.map

import kotlinx.serialization.Serializable

@Serializable
enum class NodeType {
    ENTRANCE,
    SHELF,
    CHECKOUT,
    PATHWAY,
    EXIT,
    RESTROOM,
    CUSTOMER_SERVICE
}

@Serializable
data class MapNode(
    val id: String,
    val x: Float,
    val y: Float,
    val label: String? = null,
    val type: NodeType = NodeType.PATHWAY,
    val products: List<String> = emptyList(),
    val width: Float = 40f,
    val height: Float = 20f
)

@Serializable
data class MapEdge(
    val from: String,
    val to: String,
    val distance: Float? = null
)

@Serializable
data class MapData(
    val nodes: List<MapNode>,
    val edges: List<MapEdge> = emptyList(),
    val productLocations: Map<String, List<String>> = emptyMap()
)
