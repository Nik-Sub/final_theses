package com.server.iwbi.presentation

import com.server.iwbi.webserver.WebServer
import com.iwbi.util.LifecycleComponent
import com.iwbi.util.koin.BaseKoinComponent
import com.iwbi.util.koin.ktx.declare
import com.server.iwbi.application.InputPorts
import com.server.iwbi.presentation.di.servicesModule
import com.server.iwbi.webserver.config.WebServerConfig
import com.server.iwbi.webserver.di.webServerModule
import org.koin.core.component.inject
import org.koin.dsl.koinApplication

interface PresentationLayer : LifecycleComponent

fun createPresentationLayer(
    inputPorts: InputPorts,
    webServerConfig: WebServerConfig,
): PresentationLayer = object : PresentationLayer, BaseKoinComponent() {
    override val application = koinApplication {
        declare(inputPorts)
        declare(webServerConfig)

        // Declare the KoinApplication itself to allow ktor to use it
        declare(this)

        modules(servicesModule)
        modules(webServerModule)
    }

    private val webServer: WebServer by inject()

    override fun initialize() {
        webServer.start()
    }

    override fun release() {
        webServer.stop()
    }
}
