package com.mobile.iwbi.domain.store

import com.mobile.iwbi.domain.map.StoreLayout
import com.mobile.iwbi.domain.map.MapSection
import com.mobile.iwbi.domain.map.NodeType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object StoreDataProvider {

    fun getSampleStores(): List<Store> = listOf(
        Store("SuperMart Downtown", createModernStoreLayout()),
        Store("Fresh Foods Market", createModernStoreLayout())
    )

    private fun createModernStoreLayout(): String {
        val layout = StoreLayout(
            gridWidth = 12,
            gridHeight = 10,
            sections = listOf(
                // Entrance at top center
                MapSection("entrance", "Main Entrance", "M12 2L2 7V10C2 11.1 2.9 12 4 12H20C21.1 12 22 11.1 22 10V7L12 2Z", 5, 0, 2, 1, NodeType.ENTRANCE, "#4CAF50"),

                // Left side - Fresh sections (green theme)
                MapSection("produce1", "Fresh Produce", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 0, 2, 2, 1, NodeType.SECTION, "#8BC34A",
                    listOf("apples", "bananas", "fruits")),
                MapSection("produce2", "Fresh Produce", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 0, 4, 2, 1, NodeType.SECTION, "#66BB6A",
                    listOf("carrots", "lettuce", "vegetables")),
                MapSection("produce3", "Fresh Produce", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 0, 6, 2, 1, NodeType.SECTION, "#4CAF50",
                    listOf("tomatoes", "organic")),

                // Center-left - Bakery section (orange theme)
                MapSection("bakery1", "Bakery", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 3, 2, 1, 2, NodeType.SECTION, "#FF9800",
                    listOf("bread", "croissants")),
                MapSection("bakery2", "Bakery", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 3, 5, 1, 2, NodeType.SECTION, "#FF8F00",
                    listOf("muffins", "cakes")),

                // Center - Meat & Dairy corridor (red/yellow theme)
                MapSection("meat1", "Meat & Deli", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 5, 2, 1, 1, NodeType.SECTION, "#F44336",
                    listOf("chicken", "beef")),
                MapSection("meat2", "Meat & Deli", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 6, 2, 1, 1, NodeType.SECTION, "#E53935",
                    listOf("ham", "turkey")),

                MapSection("dairy1", "Dairy", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 5, 4, 1, 1, NodeType.SECTION, "#FFEB3B",
                    listOf("milk", "cheese")),
                MapSection("dairy2", "Dairy", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 6, 4, 1, 1, NodeType.SECTION, "#FDD835",
                    listOf("yogurt", "butter")),
                MapSection("dairy3", "Dairy", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 5, 6, 2, 1, NodeType.SECTION, "#F9A825",
                    listOf("cream", "eggs")),

                // Right side - Frozen & Cold items (blue theme)
                MapSection("frozen1", "Frozen Foods", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 8, 2, 2, 1, NodeType.SECTION, "#2196F3",
                    listOf("ice cream", "frozen pizza")),
                MapSection("frozen2", "Frozen Foods", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 8, 4, 2, 1, NodeType.SECTION, "#1976D2",
                    listOf("frozen vegetables", "frozen meals")),

                // Far right - Snacks & Treats (pink theme)
                MapSection("candy1", "Candy & Snacks", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 10, 2, 2, 1, NodeType.SECTION, "#E91E63",
                    listOf("chocolate", "candy")),
                MapSection("candy2", "Candy & Snacks", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 10, 4, 2, 1, NodeType.SECTION, "#C2185B",
                    listOf("chips", "cookies")),
                MapSection("candy3", "Candy & Snacks", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 10, 6, 2, 1, NodeType.SECTION, "#AD1457",
                    listOf("snacks", "gum")),

                // Bottom aisle - Beverages (light blue theme)
                MapSection("beverages1", "Beverages", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 1, 8, 2, 1, NodeType.SECTION, "#03A9F4",
                    listOf("soda", "juice")),
                MapSection("beverages2", "Beverages", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 4, 8, 2, 1, NodeType.SECTION, "#0288D1",
                    listOf("water", "coffee")),
                MapSection("beverages3", "Beverages", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 7, 8, 2, 1, NodeType.SECTION, "#0277BD",
                    listOf("tea", "energy drinks")),

                // Add missing sections for better layout
                MapSection("bakery3", "Bakery", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 8, 6, 1, 1, NodeType.SECTION, "#EF6C00",
                    listOf("pastries", "donuts")),
                MapSection("meat3", "Meat & Deli", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 8C16 8 20 10 20 12V20C20 21.1 19.1 22 18 22H6C4.9 22 4 21.1 4 20V12C4 10 8 8 12 8Z", 7, 2, 1, 1, NodeType.SECTION, "#D32F2F",
                    listOf("bacon", "sausage")),

                // Bottom facilities row
                MapSection("customer_service", "Customer Service", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM12 7C15.3 7 18 9.7 18 13V20H6V13C6 9.7 8.7 7 12 7Z", 0, 9, 1, 1, NodeType.CUSTOMER_SERVICE, "#3F51B5"),
                MapSection("checkout1", "Checkout 1", "M7 4V2C7 0.9 7.9 0 9 0H15C16.1 0 17 0.9 17 2V4H20C21.1 4 22 4.9 22 6S21.1 8 20 8H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V8H4C2.9 8 2 7.1 2 6S2.9 4 4 4H7ZM9 2V4H15V2H9Z", 2, 9, 1, 1, NodeType.CHECKOUT, "#607D8B"),
                MapSection("checkout2", "Checkout 2", "M7 4V2C7 0.9 7.9 0 9 0H15C16.1 0 17 0.9 17 2V4H20C21.1 4 22 4.9 22 6S21.1 8 20 8H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V8H4C2.9 8 2 7.1 2 6S2.9 4 4 4H7ZM9 2V4H15V2H9Z", 4, 9, 1, 1, NodeType.CHECKOUT, "#607D8B"),
                MapSection("checkout3", "Checkout 3", "M7 4V2C7 0.9 7.9 0 9 0H15C16.1 0 17 0.9 17 2V4H20C21.1 4 22 4.9 22 6S21.1 8 20 8H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V8H4C2.9 8 2 7.1 2 6S2.9 4 4 4H7ZM9 2V4H15V2H9Z", 6, 9, 1, 1, NodeType.CHECKOUT, "#607D8B"),
                MapSection("checkout4", "Self Checkout", "M7 4V2C7 0.9 7.9 0 9 0H15C16.1 0 17 0.9 17 2V4H20C21.1 4 22 4.9 22 6S21.1 8 20 8H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V8H4C2.9 8 2 7.1 2 6S2.9 4 4 4H7ZM9 2V4H15V2H9Z", 8, 9, 1, 1, NodeType.CHECKOUT, "#607D8B"),
                MapSection("restroom", "Restrooms", "M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 1H5C3.9 1 3 1.9 3 3V7H21V9H3V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19V9Z", 10, 9, 2, 1, NodeType.RESTROOM, "#9C27B0")
            ),
            productLocations = mapOf(
                "chocolate" to listOf("candy1", "candy2", "candy3"),
                "candy" to listOf("candy1", "candy2", "candy3"),
                "milk" to listOf("dairy1", "dairy2", "dairy3"),
                "bread" to listOf("bakery1", "bakery2", "bakery3"),
                "apples" to listOf("produce1", "produce2", "produce3"),
                "ice cream" to listOf("frozen1", "frozen2"),
                "chicken" to listOf("meat1", "meat2", "meat3"),
                "soda" to listOf("beverages1", "beverages2", "beverages3")
            )
        )

        return Json.encodeToString(layout)
    }

    private fun createBoutiqueStoreLayout(): String {
        val layout = StoreLayout(
            gridWidth = 4,
            gridHeight = 6,
            sections = listOf(
                // Entrance
                MapSection("entrance", "Main Entrance", "🚪", 1, 0, 2, 1, NodeType.ENTRANCE, "#4CAF50"),

                // Premium sections
                MapSection("organic_produce", "Organic Produce", "🥬", 0, 1, 2, 1, NodeType.SECTION, "#8BC34A",
                    listOf("organic apples", "organic carrots", "organic spinach")),
                MapSection("artisan_bakery", "Artisan Bakery", "🍞", 2, 1, 2, 1, NodeType.SECTION, "#FF9800",
                    listOf("sourdough", "baguettes", "pastries")),

                MapSection("gourmet_cheese", "Gourmet Cheese", "🧀", 0, 2, 2, 1, NodeType.SECTION, "#FFF9C4",
                    listOf("brie", "cheddar", "gouda", "camembert")),
                MapSection("wine_spirits", "Wine & Spirits", "🍷", 2, 2, 2, 1, NodeType.SECTION, "#8E24AA",
                    listOf("wine", "champagne", "spirits")),

                MapSection("deli_meats", "Premium Deli", "🥩", 0, 3, 2, 1, NodeType.SECTION, "#F44336",
                    listOf("prosciutto", "salami", "premium ham")),
                MapSection("beverages", "Premium Beverages", "🧃", 2, 3, 2, 1, NodeType.SECTION, "#2196F3",
                    listOf("craft beer", "kombucha", "artisan coffee")),

                MapSection("specialty", "Specialty Items", "🎁", 0, 4, 4, 1, NodeType.SECTION, "#FF9800",
                    listOf("truffle oil", "imported pasta", "gourmet sauces")),

                // Bottom facilities
                MapSection("customer_service", "Customer Service", "ℹ️", 0, 5, 1, 1, NodeType.CUSTOMER_SERVICE, "#3F51B5"),
                MapSection("express_checkout", "Express Checkout", "🛒", 1, 5, 2, 1, NodeType.CHECKOUT, "#607D8B"),
                MapSection("restroom", "Restrooms", "🚻", 3, 5, 1, 1, NodeType.RESTROOM, "#9C27B0")
            ),
            productLocations = mapOf(
                "cheese" to listOf("gourmet_cheese"),
                "bread" to listOf("artisan_bakery"),
                "apples" to listOf("organic_produce"),
                "wine" to listOf("wine_spirits")
            )
        )

        return Json.encodeToString(layout)
    }
}
