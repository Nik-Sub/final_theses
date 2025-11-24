package ktorutils.firebase

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.request.header
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.SessionsConfig
import java.io.File
import kotlin.time.Duration.Companion.days
import org.slf4j.Logger

public class FirebaseAuthConfig : AuthenticationProvider.Config(null) {
    public var logger: Logger? = null

    public lateinit var adminFile: File

    /** The name of the session cookie */
    public lateinit var cookieName: String

    /** Directory for storing session data */
    public lateinit var sessionDirectory: File

    /** Optional secret for encrypting token session data */
    public var sessionEncryptSecret: String? = null

    /** Optional secret for signing token session data */
    public var sessionSignSecret: String? = null

    /** The expiration time of a session */
    public var sessionExpirationInSeconds: Long = 1.days.inWholeSeconds

    /**
     * Additional configurator for the [StatusPages] plugin, which is installed by this plugin
     */
    public var statusPagesConfigurator: (StatusPagesConfig.() -> Unit)? = null

    /**
     * Additional configurator for the [Sessions] plugin, which is installed by this plugin
     */
    public var sessionsConfigurator: (SessionsConfig.() -> Unit)? = null

    /**
     * Determines whether authentication failures should return a 401 instead of redirecting to the login flow.
     * The default implementation returns true for application/json requests
     */
    public var shouldRespondWith401: ApplicationCall.() -> Boolean = {
        val acceptType = request.header(HttpHeaders.Accept)
        acceptType != null && ContentType.parse(acceptType) == ContentType.Application.Json
    }

    /**
     * A validator for session-login calls. When it returns false, a 403 (forbidden) response it returned
     */
    public var sessionLoginTokenValidator: ((FirebaseToken) -> Boolean) = { true }

    /**
     * The url to redirect the user to when login is needed
     */
    public lateinit var loginUrl: String

    /**
     * The JWT realm to pass in the WWW-Authenticate header
     */
    public var realm: String? = null
}
