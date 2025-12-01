package com.mobile.iwbi.application.helloworld.di

import com.mobile.iwbi.application.helloworld.HelloWorldService
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val helloWorldModule = module {
    factory<HelloWorldServicePort> {
        println("🔧 Creating fresh HelloWorldService")
        HelloWorldService(get())
    }
}