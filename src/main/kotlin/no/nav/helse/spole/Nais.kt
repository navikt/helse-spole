package no.nav.helse.spole

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Routing.nais(collectorRegistry: PrometheusMeterRegistry) {
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