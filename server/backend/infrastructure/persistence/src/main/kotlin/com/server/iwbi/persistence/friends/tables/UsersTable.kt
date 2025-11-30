package com.server.iwbi.persistence.friends.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import java.time.Instant

object UsersTable : LongIdTable("users") {
    val userId = varchar("user_id", 255).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val displayName = varchar("display_name", 255)
    val profilePictureUrl = varchar("profile_picture_url", 500).nullable()
    val createdAt = long("created_at").default(Instant.now().toEpochMilli())
}
