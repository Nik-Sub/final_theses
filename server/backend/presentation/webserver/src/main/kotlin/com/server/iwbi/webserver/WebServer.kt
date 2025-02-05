package com.server.iwbi.webserver

import com.server.iwbi.webserver.config.WebServerConfig
import com.server.iwbi.webserver.routes.apiRoutes
import com.server.iwbi.webserver.util.configureCachingHeaders
import com.server.iwbi.webserver.util.configureCallLogging
import com.server.iwbi.webserver.util.configureCompression
import com.server.iwbi.webserver.util.configureContentNegotiation
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.defaultExceptionStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File
import kotlin.time.Duration.Companion.seconds
import org.koin.core.KoinApplication
import org.koin.ktor.plugin.setKoinApplication

class WebServer internal constructor(
    private val config: WebServerConfig,
    koinApplication: KoinApplication,
) {
    private val logger = KotlinLogging.logger { }

    private val authenticationDisabled = true//config.authentication == null

    /*private val firebaseConfig by lazy {
        requireNotNull(config.authentication).webFilePath.let { File(it).readText() }
    }*/

    private val server = embeddedServer(Netty, host = config.host, port = config.port) {
        setKoinApplication(koinApplication)

        val statusPagesConfigurator: StatusPagesConfig.() -> Unit = {
            exception { call: ApplicationCall, cause: Throwable ->
                if (defaultExceptionStatusCode(cause) == null) {
                    logger.debug(cause) {}
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
        // TODO: implement authentication on backend
        /*if (config.authentication != null) {
            install(FirebaseAuth) {
                adminFile = config.authentication.adminFile

                cookieName = "dispatcher"
                sessionDirectory = File(config.authentication.sessionDirectory)
                sessionEncryptSecret = config.authentication.sessionEncryptSecret
                sessionSignSecret = config.authentication.sessionSignSecret

                sessionLoginTokenValidator = { token ->
                    token.roles.contains(Roles.DISPATCHER)
                }

                shouldRespondWith401 = {
                    request.path().startsWith("/api")
                }

                loginUrl = "${config.baseUrl}/login"

                this.statusPagesConfigurator = statusPagesConfigurator
            }
        } else {*/
            install(StatusPages) {
                statusPagesConfigurator()
            }
        //}

        configureCallLogging()
        configureCompression()
        configureContentNegotiation()
        configureCachingHeaders()

        @Suppress("ForbiddenComment")
        // TODO: Disable for production
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Put)
            anyHost()
            allowHeader(HttpHeaders.ContentType)
        }

        routing {
            if (authenticationDisabled) {
                singlePageApplication {
                    useResources = true
                    filesPath = "public"
                    defaultPage = "index.html"
                }

                apiRoutes()
            } else {
                // TODO: implement authentication on backend
                /*get("/firebase-config") {
                    call.respondText(firebaseConfig, ContentType.Application.Json)
                }


                authenticate {
                    apiRoutes()
                }

                singlePageApplication {
                    useResources = true
                    filesPath = "public"
                    defaultPage = "index.html"
                }*/
            }
        }
    }

    fun start() {
        logger.info { "[>] Starting..." }

        // TODO: implement authentication on backend
        /*if (config.authentication == null) {
            logger.warn { "AUTHENTICATION DISABLED" }
        }*/

        server.start(wait = false)
        logger.info { "[<] Started" }
    }

    fun stop() {
        logger.info { "[>] Stopping..." }
        server.stop(2.seconds.inWholeMilliseconds, 15.seconds.inWholeMilliseconds)
        logger.info { "[<] Stopped" }
    }
}
