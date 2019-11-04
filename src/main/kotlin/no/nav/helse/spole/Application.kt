package no.nav.helse.spole

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.config.HoconApplicationConfig
import io.ktor.config.MapApplicationConfig
import io.ktor.features.CallLogging
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.request.path
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.HistorikkTjeneste
import no.nav.helse.spole.historikk.historikk
import no.nav.helse.spole.infotrygd.InfotrygdHttpIntegrasjon
import no.nav.helse.spole.infotrygd.InfotrygdPeriodeService
import no.nav.helse.spole.infotrygd.fnr.AktorregisterClient
import no.nav.helse.spole.infotrygd.fnr.StsRestClient
import no.nav.helse.spole.spa.SpaPeriodeService
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.io.FileNotFoundException
import java.net.URI

object JsonConfig {
    val accessTokenMapper: ObjectMapper =
        ObjectMapper().findAndRegisterModules().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    val infotrygdMapper: ObjectMapper = ObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
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

    val azureClientId = "/var/run/secrets/nais.io/azure/client_secret".readFile() ?: propString("azure.client.secret")
    val azure = Azure(
        clientId = azureClientId,
        clientSecret = "/var/run/secrets/nais.io/azure/client_id".readFile() ?: propString("azure.client.id"),
        scope = propString("azure.scope"),
        tenantId = propString("azure.tenant.id")
    )

    val infotrygd = InfotrygdHttpIntegrasjon(
        azure = azure,
        infotrygdRestUrl = URI(propString("infotrygd.url")),
        timeoutMS = propInt("infotrygd.timeout")
    )

    val infotrygdKilde = InfotrygdPeriodeService(fnrMapper = fnrMapper, infotrygd = infotrygd)
    val spaKilde = SpaPeriodeService()
    val historikkTjeneste = HistorikkTjeneste(infotrygd = infotrygdKilde, spa = spaKilde)

    setupAuthentication(
        jwtAudience = azureClientId
    )

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/sykepengeperioder/") }
    }

    routing {
        historikk(historikkTjeneste)
        nais(collectorRegistry)
    }
}

private fun String.readFile() =
    try {
        File(this).readText(Charsets.UTF_8)
    } catch (err: FileNotFoundException) {
        null
    }

@KtorExperimentalAPI
fun Application.propString(path: String): String = this.environment.config.property(path).getString()

@KtorExperimentalAPI
fun Application.propInt(path: String): Int = propString(path).toInt()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

typealias Fodselsnummer = String
