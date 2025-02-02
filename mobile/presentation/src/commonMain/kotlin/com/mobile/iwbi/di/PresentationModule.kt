package com.mobile.iwbi.di

import InputPorts
import com.iwbi.utils.koin.ktx.singleUsing
import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.presentation.login.LoginPanelViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.bind
import org.koin.dsl.module

val presentationModule = module {
    singleUsing<AuthenticationServicePort, InputPorts> { it.authenticationServicePort }

    viewModel {
        LoginPanelViewModel(get())
    }
}