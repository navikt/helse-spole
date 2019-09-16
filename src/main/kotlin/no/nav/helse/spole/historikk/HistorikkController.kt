package no.nav.helse.spole.historikk

import java.time.LocalDate

class HistorikkController(val infotrygd: PeriodeKilde,
                          val spa: PeriodeKilde) {

    //@GetMapping("/sykepengeperioder/{aktørId}")
    suspend fun hentPerioder(aktørId: AktørId,
                     fom: LocalDate?): Sykepengeperioder {
        val faktiskFom = fom ?: LocalDate.now().minusYears(3)
        return infotrygdPerioder(aktørId, faktiskFom).join(spaPerioder(aktørId, faktiskFom))
    }

    private suspend fun infotrygdPerioder(aktørId: AktørId, fom: LocalDate) = infotrygd.perioder(aktørId, fom)
    private suspend fun spaPerioder(aktørId: AktørId, fom: LocalDate) = spa.perioder(aktørId, fom)
}

interface PeriodeKilde {
    suspend fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder
}
