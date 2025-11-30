package com.server.iwbi.persistence.friends.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import java.time.Instant

object FriendRequestsTable : LongIdTable("friend_requests") {
    val requestId = varchar("request_id", 255).uniqueIndex()
    val fromUserId = varchar("from_user_id", 255)
    val toUserId = varchar("to_user_id", 255)
    val status = varchar("status", 50).default("PENDING")
    val createdAt = long("created_at").default(Instant.now().toEpochMilli())
    val respondedAt = long("responded_at").nullable()
}
