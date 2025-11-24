package com.mobile.iwbi.application.friends.di

import com.mobile.iwbi.application.friends.FriendService
import com.mobile.iwbi.application.friends.input.FriendServicePort
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val friendsModule = module {
    singleOf(::FriendService) bind FriendServicePort::class
}
