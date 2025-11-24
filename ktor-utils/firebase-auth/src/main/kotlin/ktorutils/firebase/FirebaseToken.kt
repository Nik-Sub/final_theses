package ktorutils.firebase

public data class FirebaseToken(
    val claims: Map<String, Any>,
    val uid: String,
    val tenantId: String?,
    val issuer: String?,
    val name: String?,
    val picture: String?,
    val email: String?,
    val isEmailVerified: Boolean,
) {
    val roles: Set<String> by lazy {
        @Suppress("UNCHECKED_CAST")
        (claims[CLAIM_ROLES] as? Collection<String>)?.toSet() ?: emptySet()
    }

    public companion object {
        public const val CLAIM_ROLES: String = "roles"
    }
}

internal fun com.google.firebase.auth.FirebaseToken.toFirebaseToken(): FirebaseToken {
    return FirebaseToken(claims, uid, tenantId, issuer, name, picture, email, isEmailVerified)
}
