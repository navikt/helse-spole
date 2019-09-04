package no.nav.helse.spole.infotrygd

import java.time.LocalDate

/* nok datadefinisjon til å hente ut relevant informasjon fra InfoTrygd sine vedtak/saker */

data class ITSykepenger(val sykemeldingsperiode: List<ITPeriode>)

data class ITPeriode(val grad: String, val sykemeldtFom: LocalDate, val sykemeldtTom: LocalDate)