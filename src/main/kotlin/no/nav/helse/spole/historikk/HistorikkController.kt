package no.nav.helse.spole.historikk

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class HistorikkController(@Autowired @Qualifier("infotrygd") val infotrygd: PeriodeKilde,
                          @Autowired @Qualifier("spa") val spa: PeriodeKilde) {

    @GetMapping("/sykepengeperioder/{aktørId}")
    fun hentPerioder(@PathVariable("aktørId") aktørId: AktørId,
                     @RequestParam("fom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fom: LocalDate?): Sykepengeperioder {
        val faktiskFom = fom ?: LocalDate.now().minusYears(3)
        println("leiter etter sykepenger fra omtrent $faktiskFom")
        return infotrygdPerioder(aktørId, faktiskFom).join(spaPerioder(aktørId, faktiskFom))
    }

    private fun infotrygdPerioder(aktørId: AktørId, fom: LocalDate) = infotrygd.perioder(aktørId, fom)
    private fun spaPerioder(aktørId: AktørId, fom: LocalDate) = spa.perioder(aktørId, fom)
}

interface PeriodeKilde {
    fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder
}
