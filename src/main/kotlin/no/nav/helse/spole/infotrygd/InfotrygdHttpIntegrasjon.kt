package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.Kilde
import no.nav.helse.spole.historikk.Periode
import no.nav.helse.spole.infotrygd.fnr.Fodselsnummer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class InfotrygdHttpIntegrasjon(val azure: Azure,
                               @Value("\$infotrygd.url") val infotrygdRestUrl: String) : InfotrygdIntegrasjon {

    override fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode> {
        println("Henter perioder fra Infotrygd")
        println("Henter Azure AD token")
        val token = azure.hentToken()
        println("Azure AD token hentet")

        val webClient = WebClient.builder().baseUrl(infotrygdRestUrl).build()
        println("Kaller Infotrygd Sykepengeliste")
        return webClient.get()
            .uri("?fnr=$fnr&fraDato=0")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer ${token.accessToken}")
            .retrieve()
            .bodyToMono(ITSykepenger::class.java).block()!!.sykemeldingsperiode.asPerioder()
    }

}

private fun List<ITPeriode>.asPerioder(): Collection<Periode> = this.map { it.toPeriode() }

private fun ITPeriode.toPeriode(): Periode = Periode(this.sykemeldtFom, this.sykemeldtTom, this.grad, Kilde.INFOTRYGD)