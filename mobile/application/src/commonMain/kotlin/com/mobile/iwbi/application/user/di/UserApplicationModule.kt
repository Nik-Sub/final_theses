package com.mobile.iwbi.application.user.di

import com.mobile.iwbi.application.user.UserRegistrationService
import com.mobile.iwbi.application.user.input.UserRegistrationServicePort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userModule = module {
    factory<UserRegistrationServicePort> {
        println("🔧 Creating fresh UserRegistrationService")
        UserRegistrationService(get())
    }
}
