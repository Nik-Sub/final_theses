package com.mobile.iwbi.domain.map

import kotlin.math.sqrt

class PathfindingService {

    data class PathResult(
        val path: List<String>,
        val totalDistance: Float
    )

    fun findShortestPath(
        mapData: MapData,
        startNodeId: String,
        endNodeId: String
    ): PathResult? {
        val distances = mutableMapOf<String, Float>()
        val previous = mutableMapOf<String, String?>()
        val unvisited = mutableSetOf<String>()

        // Initialize distances
        mapData.nodes.forEach { node ->
            distances[node.id] = Float.MAX_VALUE
            previous[node.id] = null
            unvisited.add(node.id)
        }

        distances[startNodeId] = 0f

        while (unvisited.isNotEmpty()) {
            // Find unvisited node with minimum distance
            val current = unvisited.minByOrNull { distances[it] ?: Float.MAX_VALUE }
                ?: break

            if (current == endNodeId) break

            unvisited.remove(current)

            // Check neighbors
            val neighbors = mapData.edges.filter { it.from == current || it.to == current }

            neighbors.forEach { edge ->
                val neighbor = if (edge.from == current) edge.to else edge.from

                if (neighbor in unvisited) {
                    val edgeDistance = edge.distance ?: calculateDistance(
                        mapData.nodes.find { it.id == current }!!,
                        mapData.nodes.find { it.id == neighbor }!!
                    )

                    val newDistance = (distances[current] ?: Float.MAX_VALUE) + edgeDistance

                    if (newDistance < (distances[neighbor] ?: Float.MAX_VALUE)) {
                        distances[neighbor] = newDistance
                        previous[neighbor] = current
                    }
                }
            }
        }

        // Reconstruct path
        val path = mutableListOf<String>()
        var current: String? = endNodeId

        while (current != null) {
            path.add(0, current)
            current = previous[current]
        }

        return if (path.first() == startNodeId) {
            PathResult(path, distances[endNodeId] ?: Float.MAX_VALUE)
        } else {
            null
        }
    }

    fun findProductLocations(mapData: MapData, productName: String): List<String> {
        return mapData.productLocations[productName.lowercase()]
            ?: mapData.nodes.filter { node ->
                node.products.any { it.lowercase().contains(productName.lowercase()) }
            }.map { it.id }
    }

    fun findNearestProductLocation(
        mapData: MapData,
        startNodeId: String,
        productName: String
    ): PathResult? {
        val productLocations = findProductLocations(mapData, productName)

        if (productLocations.isEmpty()) return null

        return productLocations.mapNotNull { location ->
            findShortestPath(mapData, startNodeId, location)
        }.minByOrNull { it.totalDistance }
    }

    private fun calculateDistance(node1: MapNode, node2: MapNode): Float {
        val dx = node2.x - node1.x
        val dy = node2.y - node1.y
        return sqrt(dx * dx + dy * dy)
    }
}
