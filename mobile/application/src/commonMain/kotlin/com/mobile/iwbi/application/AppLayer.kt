package com.mobile.iwbi.application

import com.iwbi.util.koin.BaseKoinComponent
import com.mobile.iwbi.application.authentication.di.authenticationModule
import com.mobile.iwbi.application.di.inputPortsModule
import com.mobile.iwbi.application.di.outputPortsModule
import com.mobile.iwbi.application.friends.di.friendsModule
import com.mobile.iwbi.application.helloworld.di.helloWorldModule
import com.mobile.iwbi.application.shoppingnotes.di.shoppingNotesModule
import com.mobile.iwbi.application.templates.di.templatesModule
import com.mobile.iwbi.application.user.di.userModule
import org.koin.core.component.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module

interface AppLayer {
    val inputPorts: InputPorts
}

fun createApplicationLayer(
    outputPorts: OutputPorts
) : AppLayer = object : AppLayer, BaseKoinComponent() {
    override val application = koinApplication {
        modules(module {
            single { outputPorts }
        })

        modules(inputPortsModule)
        modules(outputPortsModule)

        modules(authenticationModule)
        modules(helloWorldModule)
        modules(shoppingNotesModule)
        modules(templatesModule)
        modules(friendsModule)
        modules(userModule)
    }
    override val inputPorts by inject<InputPorts>()
}