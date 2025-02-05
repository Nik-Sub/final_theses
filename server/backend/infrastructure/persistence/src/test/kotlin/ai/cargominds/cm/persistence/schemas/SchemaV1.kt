package ai.cargominds.cm.persistence.schemas

import ai.cargominds.cm.persistence.schemas.Schema.Companion.schema
import org.intellij.lang.annotations.Language

val schemaV1 by lazy {
    schema {
        addTable("load_analytic_best_outbound_destinations", loadAnalyticsBestOutboundDestinations)
        addTable("load_analytic_searches", loadAnalyticSearches)
        addTable("user_loads", userLoads)
        addTable("properties", properties)
        addTable("zip_codes", zipCodes)
        addTable("toll", tollTable)
        addTable("route_toll", routeTollTable)
        addTable("market_areas", marketAreas)
        addTable("market_to_market_cost", marketToMarketCost)
        addTable("market_to_market_route", marketToMarketRoute)
    }
}

@Language("mysql")
private val loadAnalyticsBestOutboundDestinations = """
CREATE TABLE `load_analytic_best_outbound_destinations` (
  `outbound_destination_id` int NOT NULL AUTO_INCREMENT,
  `load_analytic_search_id` int NOT NULL,
  `destination_name` varchar(100) NOT NULL,
  `destination_zip` varchar(50) NOT NULL,
  `distance` double NOT NULL,
  `avg_price` double NOT NULL,
  `avg_rate` double NOT NULL,
  `destination_potential` varchar(20) NOT NULL,
  PRIMARY KEY (`outbound_destination_id`,`load_analytic_search_id`),
  KEY `fk_load_analytic_best_outbound_destinations_load_analytic_search` (`load_analytic_search_id`),
  CONSTRAINT `fk_load_analytic_best_outbound_destinations_load_analytic_search` FOREIGN KEY (`load_analytic_search_id`) REFERENCES `load_analytic_searches` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
""".trimIndent()

@Language("mysql")
private val loadAnalyticSearches = """
CREATE TABLE `load_analytic_searches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(2000) NOT NULL,
  `departure_zip` varchar(20) NOT NULL,
  `dropoff_zip` varchar(20) NOT NULL,
  `trailer_type` varchar(100) NOT NULL,
  `search_radius` double NOT NULL,
  `prices_potential` varchar(100) NOT NULL,
  `average_load_price_min` double NOT NULL,
  `average_load_price_avg` double NOT NULL,
  `average_load_price_max` double NOT NULL,
  `average_rate_min` double NOT NULL,
  `average_rate_avg` double NOT NULL,
  `average_rate_max` double NOT NULL,
  `estimated_trip_cost_total` double NOT NULL,
  `estimated_trip_cost_fuel` double NOT NULL,
  `estimated_trip_cost_tolls` double NOT NULL,
  `estimated_trip_cost_maintenance` double NOT NULL,
  `inbound_load_dynamic` varchar(100) NOT NULL,
  `outbound_load_dynamic` varchar(100) NOT NULL,
  `search_date` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
)
""".trimIndent()

@Language("mysql")
private val userLoads = """
CREATE TABLE `user_loads` (
  `loads_table_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `trailer_type` varchar(255) NOT NULL,
  `pickup_zip` varchar(5) NOT NULL,
  `pickup_time` datetime(6) NOT NULL,
  `dropoff_zip` varchar(5) NOT NULL,
  `dropoff_time` datetime(6) NOT NULL,
  `mileage` double NOT NULL,
  `price` double NOT NULL,
  KEY `user_loads_user_id` (`user_id`)
)
""".trimIndent()

@Language("mysql")
private val tollTable = """
CREATE TABLE `toll` (
  `id` bigint NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `road` varchar(1000) NOT NULL,
  `name` varchar(300) NOT NULL,
  PRIMARY KEY (`id`)
)
""".trimIndent()

@Language("mysql")
@Suppress("LongLine")
private val routeTollTable = """
CREATE TABLE `route_toll` (
  `routeid` int NOT NULL,
  `tollid` bigint NOT NULL,
  UNIQUE KEY `route_toll_routeid_tollid_unique` (`routeid`,`tollid`),
  KEY `fk_route_toll_tollid__id` (`tollid`),
  CONSTRAINT `fk_route_toll_routeid__id` FOREIGN KEY (`routeid`) REFERENCES `market_to_market_route` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_route_toll_tollid__id` FOREIGN KEY (`tollid`) REFERENCES `toll` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
)
""".trimIndent()

@Language("mysql")
private val marketAreas = """
CREATE TABLE `market_areas` (
  `my_row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `city` text,
  `state_prov` text,
  `kma` text,
  `full_name` text,
  `zips` text,
  `parent` text,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  PRIMARY KEY (`my_row_id`)
)
""".trimIndent()

@Language("mysql")
private val marketToMarketCost = """
CREATE TABLE `market_to_market_cost` (
  `city1` varchar(20) NOT NULL,
  `city2` varchar(20) NOT NULL,
  `distance` double DEFAULT NULL,
  `toll_cost` double DEFAULT NULL,
  `fuel_volume_gallons` double DEFAULT NULL,
  `date_updated` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`city1`,`city2`)
)
""".trimIndent()

@Language("mysql")
@Suppress("LongLine")
private val marketToMarketRoute = """
CREATE TABLE `market_to_market_route` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city1` varchar(20) NOT NULL,
  `city2` varchar(20) NOT NULL,
  `distance` double NOT NULL DEFAULT '0',
  `toll_cost` double NOT NULL DEFAULT '0',
  `fuel_volume_gallons` double NOT NULL DEFAULT '0',
  `polyline` mediumtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_markettomarketroutetable_city1_city2` (`city1`,`city2`),
  CONSTRAINT `fk_markettomarketroutetable_city1_city2` FOREIGN KEY (`city1`, `city2`) REFERENCES `market_to_market_cost` (`city1`, `city2`) ON DELETE CASCADE ON UPDATE RESTRICT
)
""".trimIndent()

@Language("mysql")
private val properties = """
CREATE TABLE `properties` (
  `id` int NOT NULL AUTO_INCREMENT,
  `key` varchar(256) NOT NULL,
  `value` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `properties_key_unique` (`key`)
)
""".trimIndent()

@Language("mysql")
private val zipCodes = """
CREATE TABLE `zip_codes` (
  `zip` int NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `decommissioned` int DEFAULT NULL,
  `primary_city` varchar(100) DEFAULT NULL,
  `acceptable_cities` text,
  `unacceptable_cities` text,
  `state` char(2) DEFAULT NULL,
  `county` varchar(100) DEFAULT NULL,
  `timezone` varchar(70) DEFAULT NULL,
  `area_codes` text,
  `world_region` varchar(50) DEFAULT NULL,
  `country` char(3) DEFAULT NULL,
  `latitude` decimal(10,6) DEFAULT NULL,
  `longitude` decimal(10,6) DEFAULT NULL,
  `irs_estimated_population` int DEFAULT NULL,
  PRIMARY KEY (`zip`),
  KEY `idx_zip_city_state` (`zip`,`primary_city`,`state`)
)
""".trimIndent()
