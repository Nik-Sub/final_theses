package com.mobile.iwbi.application.user.input

import com.mobile.iwbi.domain.auth.User

interface UserRegistrationServicePort {
    suspend fun registerUser(user: User): Result<Unit>
}
