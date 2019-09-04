package no.nav.helse.spole.historikk

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class HistorikkController(val infotrygd: PeriodeKilde, val spa: PeriodeKilde) {

    @GetMapping("/sykepengeperioder/{aktørid}")
    fun hentPerioder(aktørId: AktørId, fom: LocalDate = LocalDate.now().minusYears(3)): Sykepengeperioder =
        infotrygdPerioder(aktørId, fom).join(spaPerioder(aktørId, fom))

    private fun infotrygdPerioder(aktørId: AktørId, fom: LocalDate) = infotrygd.perioder(aktørId, fom)
    private fun spaPerioder(aktørId: AktørId, fom: LocalDate) = spa.perioder(aktørId, fom)
}

interface PeriodeKilde {
    fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder
}
