package no.nav.helse.spole

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.HistorikkController
import no.nav.helse.spole.infotrygd.InfotrygdHttpIntegrasjon
import no.nav.helse.spole.infotrygd.InfotrygdPeriodeService
import no.nav.helse.spole.infotrygd.fnr.AktorregisterClient
import no.nav.helse.spole.infotrygd.fnr.StsRestClient
import no.nav.helse.spole.spa.SpaPeriodeService
import java.net.URI
import java.time.LocalDate
import java.util.*

object JsonConfig {
    val objectMapper: ObjectMapper =
        ObjectMapper().findAndRegisterModules().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
}

@KtorExperimentalAPI
fun Application.spole() {

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
        endpoint = URI(propString("azure.url"))
    )

    val infotrygd = InfotrygdHttpIntegrasjon(azure = azure, infotrygdRestUrl = URI(propString("infotrygd.url")))

    val infotrygdKilde = InfotrygdPeriodeService(fnrMapper = fnrMapper, infotrygd = infotrygd)
    val spaKilde = SpaPeriodeService()
    val historikkController = HistorikkController(infotrygd = infotrygdKilde, spa = spaKilde)

    routing {
        get("/isalive") {
            call.respondText { "ALIVE" }
        }
        get("/isready") {
            call.respondText { "READY" }
        }
        get("/internal/metrics") {
            val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: Collections.emptySet()
            call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
                TextFormat.write004(this,  CollectorRegistry.defaultRegistry.filteredMetricFamilySamples(names))
            }
        }
        get("/sykepengeperioder/{aktorId}") {
            val perioder = historikkController.hentPerioder(call.parameters["aktorId"]!!, LocalDate.now().minusYears(3))
            call.respond(HttpStatusCode.OK, perioder)
        }
    }
}

@KtorExperimentalAPI
fun Application.propString(path: String): String = this.environment.config.property(path).getString()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

typealias Fodselsnummer = String