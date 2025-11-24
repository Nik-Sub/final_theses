package ktorutils.firebase

import io.ktor.server.auth.Principal

public data class FirebasePrincipal(val token: FirebaseToken) : Principal
