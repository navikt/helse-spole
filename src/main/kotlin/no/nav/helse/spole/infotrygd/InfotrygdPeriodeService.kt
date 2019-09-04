package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.historikk.AktørId
import no.nav.helse.spole.historikk.PeriodeKilde
import no.nav.helse.spole.historikk.Sykepengeperioder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class InfotrygdPeriodeService : PeriodeKilde {
    override fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder =
        Sykepengeperioder(aktørId, emptyList())
}