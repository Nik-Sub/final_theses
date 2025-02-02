package com.mobile.iwbi.application.di

import OutputPorts
import com.iwbi.utils.koin.ktx.singleUsing
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import org.koin.dsl.module

val outputPortsModule = module {
    singleUsing<AuthenticationProviderPort, OutputPorts> { it.authenticationProviderPort }
}