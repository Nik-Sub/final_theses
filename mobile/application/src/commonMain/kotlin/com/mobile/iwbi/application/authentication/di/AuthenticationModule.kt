package com.mobile.iwbi.application.authentication.di

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.authentication.AuthenticationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authenticationModule = module {
    singleOf(::AuthenticationService) bind AuthenticationServicePort::class
}