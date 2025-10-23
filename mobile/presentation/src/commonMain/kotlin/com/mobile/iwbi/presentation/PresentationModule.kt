package com.mobile.iwbi.presentation

import com.iwbi.util.koin.ktx.singleUsing
import com.mobile.iwbi.application.InputPorts

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.presentation.components.home.HomePanelViewModel
import com.mobile.iwbi.presentation.components.login.LoginPanelViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    singleUsing<AuthenticationServicePort, InputPorts> { it.authenticationServicePort }
    singleUsing<HelloWorldServicePort, InputPorts> { it.helloWorldServicePort }
    singleUsing<ShoppingNotesServicePort, InputPorts> { it.shoppingNotesServicePort }

    viewModel {
        LoginPanelViewModel(get())
    }

    viewModel {
        HomePanelViewModel(get())
    }
}