package com.mobile.iwbi.domain.map

class PathfindingService {

    data class PathResult(
        val targetSection: MapSection,
        val path: List<MapSection>,
        val description: String
    )

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
        val entrance = layout.sections.find { it.type == NodeType.ENTRANCE }

        val path = if (entrance != null) {
            generatePath(layout, entrance, targetSection)
        } else {
            listOf(targetSection)
        }

        return PathResult(
            targetSection = targetSection,
            path = path,
            description = "Found in ${targetSection.emoji} ${targetSection.name}"
        )
    }

    fun generatePath(layout: StoreLayout, start: MapSection, end: MapSection): List<MapSection> {
        // Simple pathfinding - create a path through available grid positions
        val path = mutableListOf<MapSection>()

        // Add start section
        path.add(start)

        // Generate intermediate pathway points
        val intermediatePoints = generateIntermediatePathPoints(layout, start, end)
        path.addAll(intermediatePoints)

        // Add end section
        if (start != end) {
            path.add(end)
        }

        return path
    }

    private fun generateIntermediatePathPoints(layout: StoreLayout, start: MapSection, end: MapSection): List<MapSection> {
        val pathPoints = mutableListOf<MapSection>()

        // Create pathway sections for visualization
        val currentX = start.x
        val currentY = start.y
        val targetX = end.x
        val targetY = end.y

        // Move horizontally first, then vertically (L-shaped path)
        var x = currentX
        var y = currentY

        // Horizontal movement
        while (x != targetX) {
            x += if (targetX > x) 1 else -1
            if (x != targetX || y != targetY) {
                // Only add if it's not occupied by an existing section
                val existingSection = layout.sections.find { it.x == x && it.y == y }
                if (existingSection == null) {
                    pathPoints.add(MapSection(
                        id = "path_${x}_${y}",
                        name = "Path",
                        x = x,
                        y = y,
                        type = NodeType.PATHWAY,
                        color = "#2196F3"
                    ))
                }
            }
        }

        // Vertical movement
        while (y != targetY) {
            y += if (targetY > y) 1 else -1
            if (y != targetY) {
                val existingSection = layout.sections.find { it.x == x && it.y == y }
                if (existingSection == null) {
                    pathPoints.add(MapSection(
                        id = "path_${x}_${y}",
                        name = "Path",
                        x = x,
                        y = y,
                        type = NodeType.PATHWAY,
                        color = "#2196F3"
                    ))
                }
            }
        }

        return pathPoints
    }

    fun generateVisiblePath(layout: StoreLayout, start: MapSection, end: MapSection): List<MapSection> {
        return generateIntermediatePathPoints(layout, start, end)
    }
}
