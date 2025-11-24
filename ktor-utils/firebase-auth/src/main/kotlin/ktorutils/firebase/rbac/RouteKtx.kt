package ktorutils.firebase.rbac

import io.ktor.server.application.install
import io.ktor.server.routing.Route

/**
 * Provides authorization if the user has the given role
 */
public fun Route.withRole(role: String, build: Route.() -> Unit): Route = authorizedRoute(
    requiredRoles = setOf(role),
    authType = AuthorizationType.ALL,
    build = build
)

/**
 * Provides authorization if the user has all given roles
 */
public fun Route.withRoles(role: String, vararg roles: String, build: Route.() -> Unit): Route = authorizedRoute(
    requiredRoles = roles.toSet() + role,
    authType = AuthorizationType.ALL,
    build = build
)

/**
 * Provides authorization if the user has any of the given roles
 */
public fun Route.withAnyRole(role: String, vararg roles: String, build: Route.() -> Unit): Route = authorizedRoute(
    requiredRoles = roles.toSet() + role,
    authType = AuthorizationType.ANY,
    build = build
)

/**
 * Provides authorization if the user has none of the given role
 */
public fun Route.withoutRoles(role: String, vararg roles: String, build: Route.() -> Unit): Route = authorizedRoute(
    requiredRoles = roles.toSet() + role,
    authType = AuthorizationType.NONE,
    build = build
)

private fun Route.authorizedRoute(
    requiredRoles: Set<String>,
    authType: AuthorizationType,
    build: Route.() -> Unit,
): Route {
    val description = "${authType.displayName}(${requiredRoles.joinToString(",")})"
    val authorizedRoute = createChild(AuthorizedRouteSelector(description))

    authorizedRoute.install(RoleBasedAuthorization) {
        roles = requiredRoles
        type = authType
    }
    authorizedRoute.build()
    return authorizedRoute
}
