package no.nav.helse.spole.infotrygd

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import no.nav.helse.spole.Fodselsnummer
import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.Kilde
import no.nav.helse.spole.historikk.Periode
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InfotrygdHttpIntegrasjon(
    val azure: Azure,
    val infotrygdRestUrl: URI
) : InfotrygdIntegrasjon {

    private val client = HttpClient(Apache)

    override suspend fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode> {
        println("henter sykepengeperioder fra infotrygd")
        return client.request<ITSykepenger> {
            url("${infotrygdRestUrl.toString()}?fnr=$fnr&fraDato=${fom.format(DateTimeFormatter.ISO_DATE)}")
            this.headers["Authorization"] = "Bearer ${azure.hentToken().accessToken}"
            this.method = HttpMethod.Get
        }.sykemeldingsperiode.asPerioder()
    }
}

private fun List<ITPeriode>.asPerioder(): Collection<Periode> = this.map { it.toPeriode() }
private fun ITPeriode.toPeriode(): Periode = Periode(this.sykemeldtFom, this.sykemeldtTom, this.grad, Kilde.INFOTRYGD)
