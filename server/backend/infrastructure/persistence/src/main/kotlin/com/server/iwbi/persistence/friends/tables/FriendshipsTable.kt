package com.server.iwbi.persistence.friends.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import java.time.Instant

object FriendshipsTable : LongIdTable("friendships") {
    val friendshipId = varchar("friendship_id", 255).uniqueIndex()
    val user1Id = varchar("user1_id", 255)
    val user2Id = varchar("user2_id", 255)
    val createdAt = long("created_at").default(Instant.now().toEpochMilli())
}
