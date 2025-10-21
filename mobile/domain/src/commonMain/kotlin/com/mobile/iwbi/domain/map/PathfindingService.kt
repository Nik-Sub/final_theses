package com.mobile.iwbi.domain.map

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class PathfindingService {

    data class PathResult(
        val targetSection: MapSection,
        val description: String,
        val path: List<PathNode> = emptyList()
    )

    data class PathNode(
        val x: Int,
        val y: Int,
        val isWaypoint: Boolean = false
    )

    private data class Node(
        val x: Int,
        val y: Int,
        val gCost: Double = 0.0, // Distance from start
        val hCost: Double = 0.0, // Distance to target
        val parent: Node? = null
    ) {
        val fCost: Double get() = gCost + hCost
    }

    fun findProductLocations(layout: StoreLayout, productName: String): List<MapSection> {
        return layout.productLocations[productName.lowercase()]?.mapNotNull { sectionId ->
            layout.sections.find { it.id == sectionId }
        } ?: layout.sections.filter { section ->
            section.products.any { it.lowercase().contains(productName.lowercase()) }
        }
    }

    fun findNearestProductLocation(
        layout: StoreLayout,
        productName: String
    ): PathResult? {
        val productSections = findProductLocations(layout, productName)

        if (productSections.isEmpty()) return null

        val targetSection = productSections.first()

        // Find entrance section
        val entranceSection = layout.sections.find { it.type == NodeType.ENTRANCE }

        if (entranceSection != null) {
            // Calculate path from entrance to target with proper bounds checking
            val path = findPath(layout, entranceSection, targetSection)

            return PathResult(
                targetSection = targetSection,
                description = "Found in ${targetSection.emoji} ${targetSection.name}",
                path = path
            )
        }

        return PathResult(
            targetSection = targetSection,
            description = "Found in ${targetSection.emoji} ${targetSection.name}"
        )
    }

    private fun findPath(
        layout: StoreLayout,
        start: MapSection,
        target: MapSection
    ): List<PathNode> {
        // Ensure coordinates are within valid bounds
        val startX = max(0, min(layout.gridWidth - 1, start.x + start.width / 2))
        val startY = max(0, min(layout.gridHeight - 1, start.y + start.height / 2))
        val targetX = max(0, min(layout.gridWidth - 1, target.x + target.width / 2))
        val targetY = max(0, min(layout.gridHeight - 1, target.y + target.height / 2))

        val startNode = Node(x = startX, y = startY)
        val targetNode = Node(x = targetX, y = targetY)

        val openSet = mutableListOf(startNode)
        val closedSet = mutableSetOf<Pair<Int, Int>>()

        while (openSet.isNotEmpty()) {
            // Find node with lowest fCost
            val currentNode = openSet.minByOrNull { it.fCost } ?: break
            openSet.remove(currentNode)
            closedSet.add(Pair(currentNode.x, currentNode.y))

            // Check if we reached the target
            if (currentNode.x == targetNode.x && currentNode.y == targetNode.y) {
                return reconstructPath(currentNode)
            }

            // Check only 4 directions (90-degree movement only)
            val directions = listOf(
                Pair(0, -1), // Up
                Pair(0, 1),  // Down
                Pair(-1, 0), // Left
                Pair(1, 0)   // Right
            )

            for ((dx, dy) in directions) {
                val neighborX = currentNode.x + dx
                val neighborY = currentNode.y + dy

                // Strict bounds checking to prevent out-of-bounds access
                if (neighborX < 0 || neighborX >= layout.gridWidth ||
                    neighborY < 0 || neighborY >= layout.gridHeight) {
                    continue
                }

                // Skip if already processed
                if (closedSet.contains(Pair(neighborX, neighborY))) continue

                // Check if the cell is walkable (pathway or target section)
                if (!isWalkable(layout, neighborX, neighborY, target)) continue

                val gCost = currentNode.gCost + 1.0 // Always 1.0 for 90-degree moves
                val hCost = manhattanDistance(neighborX, neighborY, targetNode.x, targetNode.y)

                val existingNode = openSet.find { it.x == neighborX && it.y == neighborY }

                if (existingNode == null) {
                    openSet.add(
                        Node(
                            x = neighborX,
                            y = neighborY,
                            gCost = gCost,
                            hCost = hCost,
                            parent = currentNode
                        )
                    )
                } else if (gCost < existingNode.gCost) {
                    openSet.remove(existingNode)
                    openSet.add(
                        Node(
                            x = neighborX,
                            y = neighborY,
                            gCost = gCost,
                            hCost = hCost,
                            parent = currentNode
                        )
                    )
                }
            }
        }

        // No path found, return simple 90-degree path with bounds checking
        return createManhattanPath(startNode, targetNode, layout)
    }

    private fun isWalkable(layout: StoreLayout, x: Int, y: Int, target: MapSection): Boolean {
        // Double-check bounds to prevent array access errors
        if (x < 0 || x >= layout.gridWidth || y < 0 || y >= layout.gridHeight) {
            return false
        }

        // Check if this position is in a pathway or is the target section
        val sectionAtPosition = layout.sections.find { section ->
            x >= section.x && x < section.x + section.width &&
            y >= section.y && y < section.y + section.height
        }

        return when {
            sectionAtPosition == null -> true // Empty space is walkable
            sectionAtPosition.type == NodeType.PATHWAY -> true
            sectionAtPosition.type == NodeType.ENTRANCE -> true
            sectionAtPosition.id == target.id -> true // Target section is walkable
            else -> false
        }
    }

    private fun manhattanDistance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        return (abs(x1 - x2) + abs(y1 - y2)).toDouble()
    }

    private fun reconstructPath(endNode: Node): List<PathNode> {
        val path = mutableListOf<PathNode>()
        var currentNode: Node? = endNode

        while (currentNode != null) {
            path.add(0, PathNode(currentNode.x, currentNode.y))
            currentNode = currentNode.parent
        }

        return path
    }

    private fun createManhattanPath(start: Node, target: Node, layout: StoreLayout): List<PathNode> {
        val path = mutableListOf<PathNode>()

        if (start.x == target.x && start.y == target.y) {
            // Same position - return single point
            val safeX = max(0, min(layout.gridWidth - 1, start.x))
            val safeY = max(0, min(layout.gridHeight - 1, start.y))
            return listOf(PathNode(safeX, safeY))
        }

        var currentX = start.x
        var currentY = start.y

        // Add starting point
        path.add(PathNode(currentX, currentY))

        // Move horizontally first (left or right)
        while (currentX != target.x) {
            currentX += if (target.x > currentX) 1 else -1
            // Ensure bounds
            val safeX = max(0, min(layout.gridWidth - 1, currentX))
            val safeY = max(0, min(layout.gridHeight - 1, currentY))
            path.add(PathNode(safeX, safeY))
        }

        // Then move vertically (up or down)
        while (currentY != target.y) {
            currentY += if (target.y > currentY) 1 else -1
            // Ensure bounds
            val safeX = max(0, min(layout.gridWidth - 1, currentX))
            val safeY = max(0, min(layout.gridHeight - 1, currentY))
            path.add(PathNode(safeX, safeY))
        }

        return path
    }
}
