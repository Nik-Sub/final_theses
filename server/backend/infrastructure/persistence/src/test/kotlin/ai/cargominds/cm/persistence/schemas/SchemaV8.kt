package ai.cargominds.cm.persistence.schemas

import org.intellij.lang.annotations.Language

val schemaV8 by lazy {
    schemaV7.extend {
        updateTable("market_areas") {
            market_areas
        }
        updateTable("zip_codes") {
            zipCodes
        }
    }
}

@Language("mysql")
private val zipCodes = """
CREATE TABLE `zip_codes` (
  `zip` varchar(5) NOT NULL,
  `type` varchar(50) NOT NULL,
  `decommissioned` int NOT NULL,
  `primary_city` varchar(100) NOT NULL,
  `acceptable_cities` text NOT NULL,
  `unacceptable_cities` text NOT NULL,
  `state` char(2) NOT NULL,
  `county` varchar(100) NOT NULL,
  `timezone` varchar(70) NOT NULL,
  `area_codes` text NOT NULL,
  `world_region` varchar(50) NOT NULL,
  `country` char(3) NOT NULL,
  `latitude` decimal(10,6) NOT NULL,
  `longitude` decimal(10,6) NOT NULL,
  `irs_estimated_population` int NOT NULL,
  `kma` varchar(6) DEFAULT NULL,
  `is_kma_center` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`zip`),
  KEY `idx_zip_city_state` (`zip`,`primary_city`,`state`),
  KEY `idx_kma` (`kma`),
  KEY `idx_is_kma_center` (`is_kma_center`),
  CONSTRAINT `fk_kma` FOREIGN KEY (`kma`) REFERENCES `market_areas` (`kma`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `zip_length_check` CHECK ((char_length(`zip`) = 5))
)
""".trimIndent()

@Language("mysql")
private val market_areas = """
CREATE TABLE `market_areas` (
  `my_row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `city` varchar(20) NOT NULL,
  `state_prov` varchar(2) NOT NULL,
  `kma` varchar(6) NOT NULL,
  `full_name` text NOT NULL,
  `zips` text NOT NULL,
  `parent` text NOT NULL,
  PRIMARY KEY (`my_row_id`),
  UNIQUE KEY `market_areas_city_unique` (`city`),
  UNIQUE KEY `market_areas_kma_unique` (`kma`)
)
""".trimIndent()
