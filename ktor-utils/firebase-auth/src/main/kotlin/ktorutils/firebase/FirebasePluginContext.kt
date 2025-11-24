package ktorutils.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth

internal class FirebasePluginContext(val config: FirebaseAuthConfig) {
    private val firebaseApp: FirebaseApp by lazy {
        FirebaseApp.initializeApp(
            FirebaseOptions.builder().run {
                val credentials = GoogleCredentials.fromStream(config.adminFile.inputStream())
                setCredentials(credentials)
                build()
            }
        )
    }

    val auth: FirebaseAuth by lazy {
        com.google.firebase.auth.FirebaseAuth.getInstance(firebaseApp)
    }
}
