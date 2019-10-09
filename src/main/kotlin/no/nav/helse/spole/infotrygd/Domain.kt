package no.nav.helse.spole.infotrygd

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.helse.spole.historikk.Utbetalingsgrad
import java.time.LocalDate

/* nok datadefinisjon til å hente ut relevant informasjon fra InfoTrygd sine vedtak/saker */

data class ITSykepenger(val sykmeldingsperioder: List<ITPeriode>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ITPeriode(val grad: String,
                     val sykemeldtFom: LocalDate,
                     val sykemeldtTom: LocalDate,
                     val graderingList: List<ITGradering>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ITGradering(
    val gradertFom: LocalDate,
    val gradertTom: LocalDate,
    val grad: Utbetalingsgrad
)