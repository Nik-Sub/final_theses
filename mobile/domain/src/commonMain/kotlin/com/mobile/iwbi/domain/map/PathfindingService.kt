package com.mobile.iwbi.domain.map

import kotlin.math.abs
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
            // Calculate path from entrance to target
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
        val startNode = Node(
            x = start.x + start.width / 2,
            y = start.y + start.height / 2
        )
        val targetNode = Node(
            x = target.x + target.width / 2,
            y = target.y + target.height / 2
        )

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

            // Check all neighbors
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) continue

                    val neighborX = currentNode.x + dx
                    val neighborY = currentNode.y + dy

                    // Check bounds
                    if (neighborX < 0 || neighborX >= layout.gridWidth ||
                        neighborY < 0 || neighborY >= layout.gridHeight) {
                        continue
                    }

                    // Skip if already processed
                    if (closedSet.contains(Pair(neighborX, neighborY))) continue

                    // Check if the cell is walkable (pathway or target section)
                    if (!isWalkable(layout, neighborX, neighborY, target)) continue

                    val gCost = currentNode.gCost + if (dx != 0 && dy != 0) sqrt(2.0) else 1.0
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
        }

        // No path found, return direct line
        return createDirectPath(startNode, targetNode)
    }

    private fun isWalkable(layout: StoreLayout, x: Int, y: Int, target: MapSection): Boolean {
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

    private fun createDirectPath(start: Node, target: Node): List<PathNode> {
        val path = mutableListOf<PathNode>()
        val steps = maxOf(abs(target.x - start.x), abs(target.y - start.y))

        if (steps == 0) return listOf(PathNode(start.x, start.y))

        for (i in 0..steps) {
            val progress = i.toDouble() / steps
            val x = (start.x + (target.x - start.x) * progress).toInt()
            val y = (start.y + (target.y - start.y) * progress).toInt()
            path.add(PathNode(x, y))
        }

        return path
    }
}
