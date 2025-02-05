package com.mobile.iwbi.application.di

import com.iwbi.util.koin.ktx.singleUsing
import com.mobile.iwbi.application.OutputPorts

import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.application.helloworld.output.HelloWorldRepositoryPort
import org.koin.dsl.module

val outputPortsModule = module {
    singleUsing<AuthenticationProviderPort, OutputPorts> { it.authenticationProviderPort }
    singleUsing<HelloWorldRepositoryPort, OutputPorts> { it.helloWorldRepositoryPort }
}