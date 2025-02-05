package com.mobile.iwbi.application.helloworld.di

import com.mobile.iwbi.application.helloworld.HelloWorldService
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val helloWorldModule = module {
    singleOf(::HelloWorldService) bind HelloWorldServicePort::class
}