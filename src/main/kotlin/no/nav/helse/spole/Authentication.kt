package no.nav.helse.spole

import com.auth0.jwk.UrlJwkProvider
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.util.KtorExperimentalAPI
import java.net.URL

internal const val AUTH_NAME = "jwr"

@KtorExperimentalAPI
fun Application.setupAuthentication() {
    val jwtKeys = "https://login.microsoftonline.com/${propString("azure.tenant.id")}/discovery/v2.0/keys"
    val jwtIssuer = "https://sts.windows.net/${propString("azure.tenant.id")}/"
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt(AUTH_NAME) {
            realm = jwtRealm
            verifier(UrlJwkProvider(URL(jwtKeys)), jwtIssuer)
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}