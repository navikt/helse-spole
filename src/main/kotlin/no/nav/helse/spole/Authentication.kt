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

private val sparkelSykepengeperioderProd = "2a3216c1-1434-4d2d-99aa-f427fe0ec1ec"
private val sparkelSykepengeperioderPreprod = "b76c01e5-34b2-43df-a056-30e9bc504e28"
private val whiteList = listOf(sparkelSykepengeperioderProd, sparkelSykepengeperioderPreprod)

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
                val validAudience = credential.payload.audience.contains(jwtAudience)
                val validSubject = whiteList.contains(credential.payload.subject)

                if (validAudience && validSubject) {
                    JWTPrincipal(credential.payload)
                } else {
                    if (!validAudience) log.info("Invalid audience: ${credential.payload.audience}")
                    if (!validSubject) log.info("Invalid subject: ${credential.payload.subject}")
                    null
                }
            }
        }
    }
}
