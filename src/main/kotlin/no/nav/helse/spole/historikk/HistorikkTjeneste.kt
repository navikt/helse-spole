package no.nav.helse.spole.historikk

import java.time.LocalDate

class HistorikkTjeneste(val infotrygd: PeriodeKilde,
                        val spa: PeriodeKilde) {

    fun hentPerioder(aktørId: AktørId,
                     fom: LocalDate?): Sykepengeperioder {
        val faktiskFom = fom ?: LocalDate.now().minusYears(3)
        val res = infotrygdPerioder(aktørId, faktiskFom).join(spaPerioder(aktørId, faktiskFom))
        println("hentet sykepengeperioder fra alle kilder")
        return res
    }

    private fun infotrygdPerioder(aktørId: AktørId, fom: LocalDate) = infotrygd.perioder(aktørId, fom)
    private fun spaPerioder(aktørId: AktørId, fom: LocalDate) = spa.perioder(aktørId, fom)
}

interface PeriodeKilde {
    fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder
}
