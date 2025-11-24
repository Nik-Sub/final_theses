package ktorutils.firebase

import ai.cargominds.ktorutils.firebase.ktx.await
import com.google.firebase.auth.FirebaseToken
import io.ktor.http.auth.AuthScheme
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.parseAuthorizationHeader
import io.ktor.server.response.respond
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

internal class FirebaseAuthProvider(
    private val context: FirebasePluginContext,
) : AuthenticationProvider(context.config) {
    private val config = context.config

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val firebaseToken = context.call.request.parseAuthorizationHeader()?.let { header ->
            (header as? HttpAuthHeader.Single)?.let {
                runCatching {
                    authenticateToken(it.blob)
                }.onFailure {
                    config.logger?.error("Authentication failed", it)

                    context.challenge(CHALLENGE, AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
                        call.respond(createUnauthorizedResponse())
                        challenge.complete()
                    }
                    return
                }.getOrNull()
            }
        } ?: let {
            // Check if a valid session exists
            context.call.sessions.get<FirebaseSession>()?.cookie?.let {
                runCatching {
                    this.context.auth.verifySessionCookieAsync(it).await()
                }.onFailure {
                    context.call.sessions.clear<FirebaseSession>()
                    context.challenge(CHALLENGE, AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
                        call.respond(createUnauthorizedResponse())
                        challenge.complete()
                    }
                    return
                }.getOrNull()
            }
        }

        firebaseToken?.let {
            context.principal(FirebasePrincipal(it.toFirebaseToken()))
        } ?: run {
            context.challenge(CHALLENGE, AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
                call.respond(createUnauthorizedResponse())
                challenge.complete()
            }
            return
        }
    }

    private suspend fun authenticateToken(token: String): FirebaseToken? = context.auth.verifyIdTokenAsync(token).await()

    private fun createUnauthorizedResponse() =
        UnauthorizedResponse(HttpAuthHeader.bearerAuthChallenge(scheme = AuthScheme.Bearer, realm = config.realm))
}

private const val CHALLENGE = "FirebaseAuth"
