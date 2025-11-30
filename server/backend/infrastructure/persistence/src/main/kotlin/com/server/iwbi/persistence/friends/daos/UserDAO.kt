package com.server.iwbi.persistence.friends.daos

import com.iwbi.domain.user.User
import com.server.iwbi.persistence.friends.tables.UsersTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserDAO>(UsersTable)

    var userId by UsersTable.userId
    var email by UsersTable.email
    var displayName by UsersTable.displayName
    var profilePictureUrl by UsersTable.profilePictureUrl
    var createdAt by UsersTable.createdAt
}

fun UserDAO.toDomain(): User {
    return User(
        id = userId,
        email = email,
        displayName = displayName,
        profilePictureUrl = profilePictureUrl,
        createdAt = createdAt
    )
}
