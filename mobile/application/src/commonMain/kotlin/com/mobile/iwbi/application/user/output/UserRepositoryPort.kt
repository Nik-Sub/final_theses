package com.mobile.iwbi.application.user.output

import com.mobile.iwbi.domain.auth.User

interface UserRepositoryPort {
    suspend fun registerUser(user: User)
}
