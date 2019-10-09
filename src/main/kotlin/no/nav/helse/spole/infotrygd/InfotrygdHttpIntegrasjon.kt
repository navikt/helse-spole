package no.nav.helse.spole.infotrygd

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import no.nav.helse.spole.Fodselsnummer
import no.nav.helse.spole.JsonConfig
import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.Kilde
import no.nav.helse.spole.historikk.Periode
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InfotrygdHttpIntegrasjon(
    val azure: Azure,
    val infotrygdRestUrl: URI,
    val timeoutMS: Int
) : InfotrygdIntegrasjon {

    override fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode> {
        val token = azure.hentToken().accessToken
        val (_, _, result) = "${infotrygdRestUrl}?fnr=$fnr&fraDato=${fom.format(DateTimeFormatter.ISO_DATE)}"
            .httpGet()
            .authentication()
            .bearer(token)
            .timeoutRead(timeoutMS)
            .response()

        val sykepenger: ITSykepenger = JsonConfig.infotrygdMapper.readValue(result.get())
        return sykepenger.sykmeldingsperioder.asPerioder()
    }
}

fun List<ITPeriode>.asPerioder(): Collection<Periode> = this.map { it.toPeriode() }
private fun ITPeriode.toPeriode(): Periode = Periode(this.sykemeldtFom, this.sykemeldtTom, this.grad, this.graderingList.first().grad, Kilde.INFOTRYGD)
