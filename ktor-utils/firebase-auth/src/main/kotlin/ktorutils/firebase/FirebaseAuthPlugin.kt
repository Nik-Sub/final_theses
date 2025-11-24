package ktorutils.firebase

import ai.cargominds.ktorutils.firebase.ktx.await
import com.google.firebase.auth.SessionCookieOptions
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.call
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.principal
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.directorySessionStorage
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.hex
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable

public val FirebaseAuth: ApplicationPlugin<FirebaseAuthConfig> = createApplicationPlugin(
    "FirebaseAuth",
    ::FirebaseAuthConfig
) {
    application.apply {
        val config = this@createApplicationPlugin.pluginConfig
        val context = FirebasePluginContext(config)

        authentication {
            register(FirebaseAuthProvider(context))
        }
        configureSessions(config)
        configureStatusPages(config)

        routing {
            authenticate {
                get(PATH_LOGGED_IN) {
                    val principal = requireNotNull(call.principal<FirebasePrincipal>())
                    if (!config.sessionLoginTokenValidator(principal.token)) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@get
                    }
                    call.respond(HttpStatusCode.OK)
                }
                get(PATH_LOGOUT) {
                    call.sessions.clear<FirebaseSession>()
                    call.respond(HttpStatusCode.OK)
                }
            }
            post(PATH_SESSION_LOGIN) {
                val request = call.receive<SessionLoginRequest>()
                runCatching {
                    val firebaseToken = context.auth.verifyIdTokenAsync(request.idToken).await()
                    firebaseToken.validateAuthTime()

                    if (!config.sessionLoginTokenValidator(firebaseToken.toFirebaseToken())) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    val sessionCookie = context.auth.createSessionCookieAsync(
                        request.idToken,
                        SessionCookieOptions.builder()
                            .setExpiresIn(config.sessionExpirationInSeconds.seconds.inWholeMilliseconds)
                            .build()
                    ).await()

                    call.sessions.set(FirebaseSession(sessionCookie))
                    call.respond(HttpStatusCode.OK)
                }.onFailure {
                    config.logger?.debug("Session login failed: ${it.message}", it)
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}

private fun Application.configureSessions(config: FirebaseAuthConfig) {
    install(Sessions) {
        cookie<FirebaseSession>(config.cookieName, directorySessionStorage(config.sessionDirectory, true)) {
            val encryptSecretKey = config.sessionEncryptSecret?.let { hex(it) }
            val signSecretKey = config.sessionSignSecret?.let { hex(it) }
            when {
                signSecretKey != null && encryptSecretKey == null -> {
                    transform(SessionTransportTransformerMessageAuthentication(signSecretKey))
                }
                signSecretKey != null && encryptSecretKey != null -> {
                    transform(SessionTransportTransformerEncrypt(encryptSecretKey, signSecretKey))
                }
                else -> config.logger?.warn("Sessions are stored unsigned and unencrypted")
            }
            cookie.secure = true
            cookie.extensions["SameSite"] = "None"
        }
        config.sessionsConfigurator?.invoke(this)
    }
}

private fun Application.configureStatusPages(config: FirebaseAuthConfig) {
    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { call, _ ->
            if (config.shouldRespondWith401(call) || PATHS_TO_401.any { call.request.path().startsWith(it) }) {
                return@status
            }

            val redirectUrl = URLBuilder(config.loginUrl).apply {
                parameters.append("redirectUrl", call.request.uri)
            }.build()

            val session: FirebaseSession? = call.sessions.get()
            if (session != null) {
                call.sessions.clear<FirebaseSession>()
            }
            call.respondRedirect(redirectUrl)
        }

        config.statusPagesConfigurator?.invoke(this)
    }
}

private fun com.google.firebase.auth.FirebaseToken.validateAuthTime() {
    val authTime = requireNotNull(claims["auth_time"]?.toString()?.toLong()) {
        "Missing auth_time claim"
    }.seconds
    val now = System.currentTimeMillis().milliseconds
    val age = now - authTime
    check(age < 5.minutes) {
        "The auth_time of the idToken is older than 5 minutes (${age}ms)"
    }
}

@Serializable
private data class SessionLoginRequest(val idToken: String)

private const val PATH_LOGGED_IN = "/loggedIn"
private const val PATH_SESSION_LOGIN = "/sessionLogin"
private const val PATH_LOGOUT = "/logout"

private val PATHS_TO_401 = listOf(PATH_LOGGED_IN, PATH_SESSION_LOGIN)
