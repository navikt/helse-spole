package no.nav.helse.spole

import java.time.LocalDate

data class Sykepengeperioder(val aktørId: AktørId, val perioder: Collection<Periode>)

data class Periode(val fom: LocalDate, val tom: LocalDate, val grad: Grad, val kilde: Kilde)

typealias AktørId = String
typealias Grad = Int

enum class Kilde {
    INFOTRYGD,
    SPA
}