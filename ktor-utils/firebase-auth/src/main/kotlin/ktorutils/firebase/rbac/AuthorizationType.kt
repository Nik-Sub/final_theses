package ktorutils.firebase.rbac

internal enum class AuthorizationType(val displayName: String) {
    /**
     * All specified roles are required
     */
    ALL("all"),

    /**
     * Any of the specified roles is required
     */
    ANY("any"),

    /**
     * None of the specified roles are allowed
     */
    NONE("none"),
}
