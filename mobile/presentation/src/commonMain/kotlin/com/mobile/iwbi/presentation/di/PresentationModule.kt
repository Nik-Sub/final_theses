package com.mobile.iwbi.presentation.di

import com.iwbi.util.koin.ktx.singleUsing
import com.mobile.iwbi.application.InputPorts

import com.mobile.iwbi.application.authentication.input.AuthenticationServicePort
import com.mobile.iwbi.application.friends.input.FriendServicePort
import com.mobile.iwbi.application.helloworld.input.HelloWorldServicePort
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import com.mobile.iwbi.application.templates.input.TemplateServicePort
import com.mobile.iwbi.presentation.AppViewModel
import com.mobile.iwbi.presentation.components.friends.FriendsViewModel
import com.mobile.iwbi.presentation.components.friends.ShareNoteViewModel
import com.mobile.iwbi.presentation.components.login.LoginPanelViewModel
import com.mobile.iwbi.presentation.components.register.RegisterPanelViewModel
import com.mobile.iwbi.presentation.components.profile.ProfileViewModel
import com.mobile.iwbi.presentation.components.shoppingnotes.ShoppingNotesViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    singleUsing<AuthenticationServicePort, InputPorts> { it.authenticationServicePort }
    singleUsing<FriendServicePort, InputPorts> { it.friendServicePort }
    singleUsing<HelloWorldServicePort, InputPorts> { it.helloWorldServicePort }
    singleUsing<ShoppingNotesServicePort, InputPorts> { it.shoppingNotesServicePort }
    singleUsing<TemplateServicePort, InputPorts> { it.templateServicePort }

    viewModel {
        LoginPanelViewModel(get())
    }

    viewModel {
        RegisterPanelViewModel(get())
    }

    // HomePanelViewModel removed - not currently in use

    viewModel {
        FriendsViewModel(get())
    }

    viewModel {
        ShareNoteViewModel(get())
    }

    viewModel {
        ShoppingNotesViewModel(get(), get(), get(), get())
    }

    viewModel {
        AppViewModel(get())
    }

    viewModel {
        ProfileViewModel(get())
    }
}