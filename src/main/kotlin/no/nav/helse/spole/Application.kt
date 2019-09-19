package no.nav.helse.spole

import com.auth0.jwk.UrlJwkProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.http.HttpStatusCode
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.HistorikkController
import no.nav.helse.spole.infotrygd.InfotrygdHttpIntegrasjon
import no.nav.helse.spole.infotrygd.InfotrygdPeriodeService
import no.nav.helse.spole.infotrygd.fnr.AktorregisterClient
import no.nav.helse.spole.infotrygd.fnr.StsRestClient
import no.nav.helse.spole.spa.SpaPeriodeService
import java.net.URI
import java.net.URL
import java.time.LocalDate


object JsonConfig {
    val objectMapper: ObjectMapper =
        ObjectMapper().findAndRegisterModules().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
}

@KtorExperimentalAPI
fun Application.spole() {

    val collectorRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = collectorRegistry
    }

    val stsRestClient = StsRestClient(
        baseUrl = propString("sts.url"),
        password = propString("sts.password"),
        username = propString("sts.username")
    )

    val fnrMapper = AktorregisterClient(aktørregisterUrl = URI(propString("fnrkilde.url")), sts = stsRestClient)

    val azure = Azure(
        clientId = propString("azure.client.id"),
        clientSecret = propString("azure.client.secret"),
        scope = propString("azure.scope"),
        tenantId = propString("azure.tenant.id")
    )

    val infotrygd = InfotrygdHttpIntegrasjon(azure = azure, infotrygdRestUrl = URI(propString("infotrygd.url")))

    val infotrygdKilde = InfotrygdPeriodeService(fnrMapper = fnrMapper, infotrygd = infotrygd)
    val spaKilde = SpaPeriodeService()
    val historikkController = HistorikkController(infotrygd = infotrygdKilde, spa = spaKilde)

    val jwtKeys = "https://login.microsoftonline.com/${propString("azure.tenant.id")}/discovery/v2.0/keys"
    val jwtIssuer = "https://sts.windows.net/${propString("azure.tenant.id")}/"
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("jwt") {
            realm = jwtRealm
            verifier(UrlJwkProvider(URL(jwtKeys)), jwtIssuer)
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }


    routing {
        authenticate("jwt") {
            get("/sykepengeperioder/{aktorId}") {
                val perioder =
                    historikkController.hentPerioder(call.parameters["aktorId"]!!, LocalDate.now().minusYears(3))
                call.respond(HttpStatusCode.OK, JsonConfig.objectMapper.writeValueAsBytes(perioder))
            }
        }
        get("/isalive") {
            call.respondText { "ALIVE" }
        }
        get("/isready") {
            call.respondText { "READY" }
        }

        get("/internal/metrics") {
            call.respondText(collectorRegistry.scrape())
        }
    }
}

@KtorExperimentalAPI
fun Application.propString(path: String): String = this.environment.config.property(path).getString()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

typealias Fodselsnummer = String