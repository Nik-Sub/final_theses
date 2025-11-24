package ktorutils.firebase.rbac

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.log
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.principal
import io.ktor.server.request.path
import io.ktor.server.response.respond
import ktorutils.firebase.FirebasePrincipal

internal val RoleBasedAuthorization = createRouteScopedPlugin(
    "FirebaseRoleBasedAuthorizationPlugin",
    createConfiguration = ::AuthorizationConfig
) {
    val requiredRoles = pluginConfig.roles
    val type = pluginConfig.type

    on(AuthenticationChecked) { call ->
        val user = call.principal<FirebasePrincipal>() ?: return@on
        var denyReason: String? = null
        when (type) {
            AuthorizationType.ALL -> {
                val missing = requiredRoles - user.token.roles
                if (missing.isNotEmpty()) {
                    denyReason = "User ${user.token.uid} is missing required role(s): ${missing.joinToString()}"
                }
            }
            AuthorizationType.ANY -> {
                if (user.token.roles.none { it in requiredRoles }) {
                    denyReason = "User ${user.token.uid} doesn't have any of the allowed roles: ${requiredRoles.joinToString()}"
                }
            }
            AuthorizationType.NONE -> {
                if (user.token.roles.any { it in requiredRoles }) {
                    denyReason = "User ${user.token.uid} has forbidden roles: ${requiredRoles.intersect(user.token.roles).joinToString()}"
                }
            }
        }

        if (denyReason != null) {
            application.log.warn("Authorization failed for path ${call.request.path()}: $denyReason")
            call.respond(HttpStatusCode.Forbidden, denyReason)
        }
    }
}
