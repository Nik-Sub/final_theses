package ai.cargominds.cm.persistence.schemas

import org.intellij.lang.annotations.Language

val schemaV3 by lazy {
    schemaV2.extend {
        updateTable("load_analytic_searches") {
            loadAnalyticSearches
        }
        addTable("tms_loads", tmsLoads)
    }
}

@Language("mysql")
private val loadAnalyticSearches = """
CREATE TABLE `load_analytic_searches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(2000) NOT NULL,
  `departure_zip` varchar(5) NOT NULL,
  `dropoff_zip` varchar(5) NOT NULL,
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
private val tmsLoads = """
CREATE TABLE `tms_loads` (
  `id` int NOT NULL AUTO_INCREMENT,
  `original_id` varchar(255) NOT NULL,
  `company_name` varchar(255) NOT NULL,
  `origin_latitude` double NOT NULL,
  `origin_longitude` double NOT NULL,
  `origin_arrival_time` datetime(6) NOT NULL,
  `origin_departure_time` datetime(6) NOT NULL,
  `origin_zip_code` varchar(5) NOT NULL,
  `destination_latitude` double NOT NULL,
  `destination_longitude` double NOT NULL,
  `destination_arrival_time` datetime(6) NOT NULL,
  `destination_departure_time` datetime(6) NOT NULL,
  `destination_zip_code` varchar(5) NOT NULL,
  `price` double NOT NULL,
  `move_distance` double NOT NULL,
  `fuel_distance` double NOT NULL,
  `trailer_type` varchar(255) DEFAULT NULL,
  `ordered_timestamp` datetime(6) NOT NULL,
  `inserted_timestamp` datetime(6) NOT NULL,
  `metadata` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tms_loads_original_id_company_name_unique` (`original_id`,`company_name`),
  KEY `tms_loads_trailer_type` (`trailer_type`),
  KEY `tms_loads_inserted_timestamp` (`inserted_timestamp`)
)
""".trimIndent()
