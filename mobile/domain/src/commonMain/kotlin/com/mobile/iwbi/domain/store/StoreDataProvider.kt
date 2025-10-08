package com.mobile.iwbi.domain.store

import com.mobile.iwbi.domain.map.MapData
import com.mobile.iwbi.domain.map.MapEdge
import com.mobile.iwbi.domain.map.MapNode
import com.mobile.iwbi.domain.map.NodeType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object StoreDataProvider {

    fun getSampleStores(): List<Store> = listOf(
        Store("SuperMart Downtown", createSuperMartMap()),
        Store("Fresh Foods Market", createFreshFoodsMap())
    )

    private fun createSuperMartMap(): String {
        val mapData = MapData(
            nodes = listOf(
                // Entrance
                MapNode("entrance", 100f, 50f, "Main Entrance", NodeType.ENTRANCE, width = 60f, height = 30f),

                // Pathways
                MapNode("path_main", 100f, 150f, "", NodeType.PATHWAY, width = 40f, height = 40f),
                MapNode("path_left", 50f, 250f, "", NodeType.PATHWAY, width = 40f, height = 40f),
                MapNode("path_center", 150f, 250f, "", NodeType.PATHWAY, width = 40f, height = 40f),
                MapNode("path_right", 250f, 250f, "", NodeType.PATHWAY, width = 40f, height = 40f),
                MapNode("path_back", 150f, 350f, "", NodeType.PATHWAY, width = 40f, height = 40f),

                // Shelves
                MapNode("shelf_produce", 50f, 200f, "Fresh Produce", NodeType.SHELF,
                    listOf("apples", "bananas", "carrots", "lettuce"), 80f, 40f),
                MapNode("shelf_dairy", 150f, 200f, "Dairy Section", NodeType.SHELF,
                    listOf("milk", "cheese", "yogurt", "butter"), 80f, 40f),
                MapNode("shelf_bakery", 250f, 200f, "Bakery", NodeType.SHELF,
                    listOf("bread", "croissants", "muffins", "cakes"), 80f, 40f),
                MapNode("shelf_candy", 100f, 300f, "Candy & Chocolate", NodeType.SHELF,
                    listOf("chocolate", "candy", "gum", "cookies"), 80f, 40f),
                MapNode("shelf_beverages", 200f, 300f, "Beverages", NodeType.SHELF,
                    listOf("soda", "juice", "water", "coffee"), 80f, 40f),
                MapNode("shelf_frozen", 50f, 400f, "Frozen Foods", NodeType.SHELF,
                    listOf("ice cream", "frozen pizza", "frozen vegetables"), 80f, 40f),
                MapNode("shelf_meat", 250f, 400f, "Meat & Deli", NodeType.SHELF,
                    listOf("chicken", "beef", "ham", "turkey"), 80f, 40f),

                // Checkout
                MapNode("checkout_1", 100f, 450f, "Checkout 1", NodeType.CHECKOUT, width = 60f, height = 30f),
                MapNode("checkout_2", 160f, 450f, "Checkout 2", NodeType.CHECKOUT, width = 60f, height = 30f),

                // Other facilities
                MapNode("customer_service", 300f, 100f, "Customer Service", NodeType.CUSTOMER_SERVICE, width = 80f, height = 30f),
                MapNode("restroom", 300f, 350f, "Restroom", NodeType.RESTROOM, width = 60f, height = 30f)
            ),
            edges = listOf(
                // Main path connections
                MapEdge("entrance", "path_main", 100f),
                MapEdge("path_main", "path_left", 70f),
                MapEdge("path_main", "path_center", 50f),
                MapEdge("path_main", "path_right", 150f),
                MapEdge("path_center", "path_back", 100f),
                MapEdge("path_left", "path_center", 100f),
                MapEdge("path_center", "path_right", 100f),

                // Shelf connections
                MapEdge("path_left", "shelf_produce", 50f),
                MapEdge("path_center", "shelf_dairy", 50f),
                MapEdge("path_right", "shelf_bakery", 50f),
                MapEdge("path_back", "shelf_candy", 50f),
                MapEdge("path_back", "shelf_beverages", 50f),
                MapEdge("path_back", "shelf_frozen", 100f),
                MapEdge("path_back", "shelf_meat", 100f),

                // Checkout connections
                MapEdge("path_back", "checkout_1", 100f),
                MapEdge("path_back", "checkout_2", 100f),

                // Facility connections
                MapEdge("path_main", "customer_service", 200f),
                MapEdge("path_right", "restroom", 100f)
            ),
            productLocations = mapOf(
                "chocolate" to listOf("shelf_candy"),
                "candy" to listOf("shelf_candy"),
                "milk" to listOf("shelf_dairy"),
                "bread" to listOf("shelf_bakery"),
                "apples" to listOf("shelf_produce"),
                "ice cream" to listOf("shelf_frozen"),
                "chicken" to listOf("shelf_meat"),
                "soda" to listOf("shelf_beverages")
            )
        )

        return Json.encodeToString(mapData)
    }

    private fun createFreshFoodsMap(): String {
        val mapData = MapData(
            nodes = listOf(
                MapNode("entrance", 80f, 50f, "Main Entrance", NodeType.ENTRANCE, width = 60f, height = 30f),
                MapNode("path_1", 80f, 120f, "", NodeType.PATHWAY, width = 40f, height = 40f),
                MapNode("path_2", 160f, 180f, "", NodeType.PATHWAY, width = 40f, height = 40f),
                MapNode("organic_produce", 80f, 180f, "Organic Produce", NodeType.SHELF,
                    listOf("organic apples", "organic carrots", "organic spinach"), 90f, 40f),
                MapNode("gourmet_cheese", 240f, 180f, "Gourmet Cheese", NodeType.SHELF,
                    listOf("brie", "cheddar", "gouda", "camembert"), 90f, 40f),
                MapNode("artisan_bakery", 160f, 280f, "Artisan Bakery", NodeType.SHELF,
                    listOf("sourdough", "baguettes", "pastries"), 90f, 40f),
                MapNode("checkout", 160f, 350f, "Express Checkout", NodeType.CHECKOUT, width = 80f, height = 30f)
            ),
            edges = listOf(
                MapEdge("entrance", "path_1", 70f),
                MapEdge("path_1", "organic_produce", 40f),
                MapEdge("path_1", "path_2", 100f),
                MapEdge("path_2", "gourmet_cheese", 80f),
                MapEdge("path_2", "artisan_bakery", 100f),
                MapEdge("artisan_bakery", "checkout", 70f)
            ),
            productLocations = mapOf(
                "cheese" to listOf("gourmet_cheese"),
                "bread" to listOf("artisan_bakery"),
                "apples" to listOf("organic_produce")
            )
        )

        return Json.encodeToString(mapData)
    }
}
