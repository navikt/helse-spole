package no.nav.helse.spole.historikk

import java.time.LocalDate

data class Sykepengeperioder(val aktørId: AktørId, val perioder: Collection<Periode>)

// om refusjon: ha med et utbetalt til orgnummer
data class Periode(val fom: LocalDate, val tom: LocalDate, val grad: Grad, val kilde: Kilde)

typealias AktørId = String
typealias Grad = Int

enum class Kilde {
    INFOTRYGD,
    SPA
}

fun Sykepengeperioder.join(other: Sykepengeperioder): Sykepengeperioder {
    return if (this.aktørId != other.aktørId) throw RuntimeException("Kan ikke slå sammen sykepengeperioder fra forskjellige aktører.")
    else Sykepengeperioder(this.aktørId, perioder.plus(other.perioder))
}