package ai.cargominds.cm.persistence.schemas

import org.intellij.lang.annotations.Language

val schemaV4 by lazy {
    schemaV3.extend {
        updateTable("market_to_market_cost") {
            marketToMarketCostTable
        }
        updateTable("market_to_market_route") {
            marketToMarketRouteTable
        }
    }
}

@Language("mysql")
private val marketToMarketCostTable = """
CREATE TABLE `market_to_market_cost` (
  `city1` varchar(20) NOT NULL,
  `city2` varchar(20) NOT NULL,
  `distance` double DEFAULT NULL,
  `toll_cost` double DEFAULT NULL,
  `fuel_volume_gallons` double DEFAULT NULL,
  `duration_seconds` int NOT NULL DEFAULT '0',
  `date_updated` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`city1`,`city2`)
)
""".trimIndent()

@Language("mysql")
@Suppress("LongLine")
private val marketToMarketRouteTable = """
CREATE TABLE `market_to_market_route` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city1` varchar(20) NOT NULL,
  `city2` varchar(20) NOT NULL,
  `distance` double NOT NULL DEFAULT '0',
  `toll_cost` double NOT NULL DEFAULT '0',
  `fuel_volume_gallons` double NOT NULL DEFAULT '0',
  `duration_seconds` int NOT NULL DEFAULT '0',
  `polyline` mediumtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_markettomarketroutetable_city1_city2` (`city1`,`city2`),
  CONSTRAINT `fk_markettomarketroutetable_city1_city2` FOREIGN KEY (`city1`, `city2`) REFERENCES `market_to_market_cost` (`city1`, `city2`) ON DELETE CASCADE ON UPDATE RESTRICT
)
""".trimIndent()
