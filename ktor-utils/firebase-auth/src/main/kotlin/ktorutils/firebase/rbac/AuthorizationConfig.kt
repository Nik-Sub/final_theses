package ktorutils.firebase.rbac

internal class AuthorizationConfig {
    var roles: Set<String> = emptySet()
    lateinit var type: AuthorizationType
}
