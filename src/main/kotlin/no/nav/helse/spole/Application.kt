package no.nav.helse.spole

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.ktor.application.Application
import io.ktor.application.install
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
import java.net.URI

object JsonConfig {
    val accessTokenMapper: ObjectMapper =
        ObjectMapper().findAndRegisterModules().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    val infotrygdMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
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

    val infotrygd = InfotrygdHttpIntegrasjon(
        azure = azure,
        infotrygdRestUrl = URI(propString("infotrygd.url")),
        timeoutMS = propInt("infotrygd.timeout")
    )

    val infotrygdKilde = InfotrygdPeriodeService(fnrMapper = fnrMapper, infotrygd = infotrygd)
    val spaKilde = SpaPeriodeService()
    val historikkTjeneste = HistorikkTjeneste(infotrygd = infotrygdKilde, spa = spaKilde)

    setupAuthentication()

    install(CallLogging) {
        level = Level.INFO
        logger = LoggerFactory.getLogger("tjenestekall")
        filter { call -> call.request.path().startsWith("/sykepengeperioder/") }
    }

    routing {
        historikk(historikkTjeneste)
        nais(collectorRegistry)
    }
}


@KtorExperimentalAPI
fun Application.propString(path: String): String = this.environment.config.property(path).getString()

@KtorExperimentalAPI
fun Application.propInt(path: String): Int = propString(path).toInt()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

typealias Fodselsnummer = String