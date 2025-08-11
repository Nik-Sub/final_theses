package com.mobile.iwbi.domain.map

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MapNode(val id: String, val x: Float, val y: Float, val label: String? = null)

@Serializable
data class MapEdge(val from: String, val to: String)

@Serializable
data class MapData(val nodes: List<MapNode>, val edges: List<MapEdge> = emptyList())
