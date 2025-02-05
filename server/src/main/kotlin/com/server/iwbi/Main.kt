@file:JvmName("Main")

package com.server.iwbi

import com.server.iwbi.backend.App
import com.server.iwbi.backend.config.Config
import com.server.iwbi.backend.di.appModule
import com.server.iwbi.backend.di.configModule
import java.io.File
import kotlin.system.exitProcess
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

fun main(args: Array<String>) {
    // We'll manually shut down the logger
    System.setProperty("log4j.shutdownHookEnabled", "false")

    val koinApp = startKoin {
        module {
            modules(
                appModule,
                module { single { readConfig(args) } },
                configModule,
            )
        }
    }

    val app = koinApp.koin.get<App>()

    // Try to cleanly stop application when we are shut down
    addShutdownHook {
        app.stop()
        koinApp.close()
    }

    app.start()

    Thread.currentThread().join()
}

private fun readConfig(args: Array<String>): Config {
    val configFilePath = args.getOrNull(0) ?: run {
        println("Missing config file argument")
        exitProcess(1)
    }

    val configFile = File(configFilePath)
    if (!configFile.exists()) {
        println("Config file doesn't exist: ${configFile.path}")
        exitProcess(1)
    }

    return Json.Default.decodeFromString(Config.serializer(), configFile.readText())
}

private fun addShutdownHook(action: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(
        object : Thread() {
            override fun run() {
                action()
            }
        }
    )
}
