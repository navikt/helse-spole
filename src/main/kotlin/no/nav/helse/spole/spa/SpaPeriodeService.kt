package no.nav.helse.spole.spa

import no.nav.helse.spole.historikk.AktørId
import no.nav.helse.spole.historikk.PeriodeKilde
import no.nav.helse.spole.historikk.Sykepengeperioder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SpaPeriodeService : PeriodeKilde {
    override fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder =
        Sykepengeperioder(aktørId, emptyList())
}