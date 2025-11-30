package com.server.iwbi.persistence.friends.daos

import com.iwbi.domain.user.Friendship
import com.server.iwbi.persistence.friends.tables.FriendshipsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class FriendshipDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<FriendshipDAO>(FriendshipsTable)

    var friendshipId by FriendshipsTable.friendshipId
    var user1Id by FriendshipsTable.user1Id
    var user2Id by FriendshipsTable.user2Id
    var createdAt by FriendshipsTable.createdAt
}

fun FriendshipDAO.toDomain(): Friendship {
    return Friendship(
        id = friendshipId,
        user1Id = user1Id,
        user2Id = user2Id,
        createdAt = createdAt
    )
}
