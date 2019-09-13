package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.Kilde
import no.nav.helse.spole.historikk.Periode
import no.nav.helse.spole.infotrygd.fnr.Fodselsnummer
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.LocalDate

@Component
class InfotrygdHttpIntegrasjon(
    val azure: Azure,
    @Value("\$infotrygd.url") val infotrygdRestUrl: String
) : InfotrygdIntegrasjon {

    override fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode> {
        val token = azure.hentToken()

        val headers = HttpHeaders()
        headers.setBearerAuth(token.accessToken)

        println("Kaller Infotrygd Sykepengeliste")
        val response = RestTemplate().exchange(
            "$infotrygdRestUrl?fnr=$fnr&fraDato=0",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            ITSykepenger::class.java
        )

        return response.body.sykemeldingsperiode.asPerioder()
    }

}

private fun List<ITPeriode>.asPerioder(): Collection<Periode> = this.map { it.toPeriode() }
private fun ITPeriode.toPeriode(): Periode = Periode(this.sykemeldtFom, this.sykemeldtTom, this.grad, Kilde.INFOTRYGD)
private fun String.asURI() = URI(this)
