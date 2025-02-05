package ai.cargominds.cm.persistence.schemas

import org.intellij.lang.annotations.Language

val schemaV2 by lazy {
    schemaV1.extend {
        updateTable("user_loads") {
            userLoads
        }
    }
}

@Language("mysql")
private val userLoads = """
CREATE TABLE `user_loads` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `trailer_type` varchar(255) NOT NULL,
  `pickup_zip` varchar(5) NOT NULL,
  `pickup_city` varchar(255) NOT NULL,
  `pickup_state_code` varchar(2) NOT NULL,
  `pickup_time` datetime(6) NOT NULL,
  `dropoff_zip` varchar(5) NOT NULL,
  `dropoff_city` varchar(255) NOT NULL,
  `dropoff_state_code` varchar(2) NOT NULL,
  `dropoff_time` datetime(6) NOT NULL,
  `mileage` double NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_loads_user_id` (`user_id`)
)
""".trimIndent()
