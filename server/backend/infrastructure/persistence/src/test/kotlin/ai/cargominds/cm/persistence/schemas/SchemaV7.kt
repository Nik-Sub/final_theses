package ai.cargominds.cm.persistence.schemas

import org.intellij.lang.annotations.Language

val schemaV7 by lazy {
    schemaV6.extend {
        addTable("feedback", feedback)
    }
}

@Language("mysql")
private val feedback = """
CREATE TABLE `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `type` varchar(255) NOT NULL,
  `inserted_timestamp` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `feedback_user_id` (`user_id`),
  KEY `feedback_type` (`type`)
)
""".trimIndent()
