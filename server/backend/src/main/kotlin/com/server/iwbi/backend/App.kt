package com.server.iwbi.backend

import com.server.iwbi.application.createApplicationLayer
import com.server.iwbi.backend.config.Config
import com.server.iwbi.infrastructure.di.createInfrastructureLayer
import com.server.iwbi.presentation.createPresentationLayer
import com.iwbi.util.CompositeLifecycleComponent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.logging.log4j.LogManager

class App(
    private val config: Config,
) {
    private val logger = KotlinLogging.logger { }

    private val compositeLifecycleComponent = CompositeLifecycleComponent()

    init {
        System.setProperty("io.ktor.server.engine.ShutdownHook", "false")
    }

    fun start() {
        logger.info { "[>] Starting..." }

        val infrastructureLayer = createInfrastructureLayer(
            //config.database,
        )
        val appLayer = createApplicationLayer(infrastructureLayer.outputPorts)
        val presentationLayer = createPresentationLayer(appLayer.inputPorts, config.webServer)

        compositeLifecycleComponent.addAll(infrastructureLayer, appLayer, presentationLayer)
        compositeLifecycleComponent.initialize()

        logger.info { "[<] Started" }
    }

    fun stop() {
        logger.info { "[>] Stopping..." }

        compositeLifecycleComponent.release()

        logger.info { "[<] Stopped" }

        LogManager.shutdown()
    }
}
