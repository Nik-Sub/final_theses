package com.server.iwbi.persistence.friends.daos

import com.iwbi.domain.user.FriendRequest
import com.iwbi.domain.user.FriendRequestStatus
import com.server.iwbi.persistence.friends.tables.FriendRequestsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class FriendRequestDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<FriendRequestDAO>(FriendRequestsTable)

    var requestId by FriendRequestsTable.requestId
    var fromUserId by FriendRequestsTable.fromUserId
    var toUserId by FriendRequestsTable.toUserId
    var status by FriendRequestsTable.status
    var createdAt by FriendRequestsTable.createdAt
    var respondedAt by FriendRequestsTable.respondedAt
}

fun FriendRequestDAO.toDomain(): FriendRequest {
    return FriendRequest(
        id = requestId,
        fromUserId = fromUserId,
        toUserId = toUserId,
        status = FriendRequestStatus.valueOf(status),
        createdAt = createdAt,
        respondedAt = respondedAt
    )
}
