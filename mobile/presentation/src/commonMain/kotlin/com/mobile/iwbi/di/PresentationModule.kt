package com.mobile.iwbi.di

import com.iwbi.util.koin.ktx.singleUsing
import com.mobile.iwbi.application.InputPorts

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.presentation.home.HomePanelViewModel
import com.mobile.iwbi.presentation.login.LoginPanelViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    singleUsing<AuthenticationServicePort, InputPorts> { it.authenticationServicePort }
    singleUsing<HelloWorldServicePort, InputPorts> { it.helloWorldServicePort }

    viewModel {
        LoginPanelViewModel(get())
    }

    viewModel {
        HomePanelViewModel(get())
    }
}