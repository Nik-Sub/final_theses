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
import io.ktor.server.auth.authenticate
import io.ktor.server.engine.defaultExceptionStatusCode
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ktorutils.firebase.FirebaseAuth
import java.io.File
import kotlin.time.Duration.Companion.seconds
import org.koin.core.KoinApplication
import org.koin.ktor.plugin.setKoinApplication

class WebServer internal constructor(
    private val config: WebServerConfig,
    koinApplication: KoinApplication,
) {
    private val logger = KotlinLogging.logger { }

    private val firebaseConfig by lazy {
        requireNotNull(config.authentication).webFilePath.let { File(it).readText() }
    }

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

        // Always install Firebase Auth to support both Bearer tokens and sessions
        install(FirebaseAuth) {
            adminFile = File(config.authentication.adminFile)

            // Session configuration for web clients
            cookieName = "iwbi"
            sessionDirectory = File(config.authentication.sessionDirectory)
            sessionEncryptSecret = config.authentication.sessionEncryptSecret
            sessionSignSecret = config.authentication.sessionSignSecret

            // Allow any authenticated Firebase user and auto-create them in database
            sessionLoginTokenValidator = { token ->
                this@WebServer.logger.info { "🔐 Server: Received Firebase token for user: ${token.uid}" }
                true // Accept any valid Firebase token - user creation will happen in routes
            }

            shouldRespondWith401 = {
                val path = request.path()
                val isApiPath = path.startsWith("/api")
                this@WebServer.logger.info { "🔍 Server: Checking auth for path: $path, isAPI: $isApiPath" }
                isApiPath
            }

            loginUrl = "${config.baseUrl}/login"

            this.statusPagesConfigurator = statusPagesConfigurator
        }

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
            allowHeader(HttpHeaders.Authorization) // Allow Authorization header for Bearer tokens
        }

        routing {
            get("/firebase-config") {
                call.respondText(firebaseConfig, ContentType.Application.Json)
            }

            // All API routes are now protected and support both authentication methods
            authenticate {
                apiRoutes()
            }

            singlePageApplication {
                useResources = true
                filesPath = "public"
                defaultPage = "index.html"
            }
        }
    }

    fun start() {
        logger.info { "[>] Starting..." }
        server.start(wait = false)
        logger.info { "[<] Started" }
    }

    fun stop() {
        logger.info { "[>] Stopping..." }
        server.stop(2.seconds.inWholeMilliseconds, 15.seconds.inWholeMilliseconds)
        logger.info { "[<] Stopped" }
    }
}
