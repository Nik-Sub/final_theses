package ai.cargominds.cm.persistence.schemas

import org.intellij.lang.annotations.Language

val schemaV6 by lazy {
    schemaV5.extend {
        updateTable("market_to_market_route") {
            marketToMarketRoute
        }
    }
}

@Language("mysql")
private val marketToMarketRoute = """
CREATE TABLE `market_to_market_route` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city1` varchar(20) NOT NULL,
  `city2` varchar(20) NOT NULL,
  `distance` double NOT NULL DEFAULT '0',
  `toll_cost` double NOT NULL DEFAULT '0',
  `fuel_volume_gallons` double NOT NULL DEFAULT '0',
  `duration_seconds` int NOT NULL DEFAULT '0',
  `date_updated` datetime(6) DEFAULT NULL,
  `is_primary` tinyint(1) NOT NULL DEFAULT '0',
  `polyline` mediumtext NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_primary_route_per_market_to_market` (`city1`,`city2`,((case when (`is_primary` = 1) then `is_primary` else NULL end))),
  KEY `fk_markettomarketroutetable_city1_city2` (`city1`,`city2`),
  CONSTRAINT `fk_markettomarketroutetable_city1_city2` FOREIGN KEY (`city1`, `city2`) REFERENCES `market_to_market_cost` (`city1`, `city2`) ON DELETE CASCADE ON UPDATE RESTRICT
)
""".trimIndent()
