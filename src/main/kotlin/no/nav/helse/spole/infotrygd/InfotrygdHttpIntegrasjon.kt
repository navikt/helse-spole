package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.Kilde
import no.nav.helse.spole.historikk.Periode
import no.nav.helse.spole.infotrygd.fnr.Fodselsnummer
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.lang.RuntimeException
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class InfotrygdHttpIntegrasjon(
    val azure: Azure,
    @Value("\${infotrygd.url}") val infotrygdRestUrl: String
) : InfotrygdIntegrasjon {

    override fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode> {
        val token = azure.hentToken()

        val headers = HttpHeaders()
        headers.setBearerAuth(token.accessToken)

        println("Kaller Infotrygd Sykepengeliste")
        println("$infotrygdRestUrl?fnr=redacted&fraDato=0")
        val response = RestTemplate().exchange(
            "$infotrygdRestUrl?fnr=$fnr&fraDato=${fom.format(DateTimeFormatter.ISO_DATE)}",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            ITSykepenger::class.java
        )

        return try { response.body.sykemeldingsperiode.asPerioder() } catch (e: HttpMessageNotReadableException) {
            throw RuntimeException("${e.message} -- ${e.httpInputMessage.bodyAsString()}")
        }

    }

}

private fun HttpInputMessage.bodyAsString(): String = String(this.body.readAllBytes())

private fun List<ITPeriode>.asPerioder(): Collection<Periode> = this.map { it.toPeriode() }
private fun ITPeriode.toPeriode(): Periode = Periode(this.sykemeldtFom, this.sykemeldtTom, this.grad, Kilde.INFOTRYGD)
private fun String.asURI() = URI(this)
