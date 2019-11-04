package no.nav.helse.spole

import com.auth0.jwk.UrlJwkProvider
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.LoggerFactory
import java.net.URL

internal const val AUTH_NAME = "jwt"

private val log = LoggerFactory.getLogger("SpoleAuthentication")

@KtorExperimentalAPI
fun Application.setupAuthentication(
    jwtAudience: String,
    jwtKeys: String = "https://login.microsoftonline.com/${propString("azure.tenant.id")}/discovery/v2.0/keys",
    jwtIssuer: String = "https://login.microsoftonline.com/${propString("azure.tenant.id")}/v2.0",
    jwtRealm: String = environment.config.property("jwt.realm").getString()
) {
    install(Authentication) {
        jwt(AUTH_NAME) {
            realm = jwtRealm
            verifier(UrlJwkProvider(URL(jwtKeys)), jwtIssuer)
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    log.info("Authenticated subject ${credential.payload.subject}")
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
