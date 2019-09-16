package no.nav.helse.spole.spa

import no.nav.helse.spole.historikk.AktørId
import no.nav.helse.spole.historikk.PeriodeKilde
import no.nav.helse.spole.historikk.Sykepengeperioder
import java.time.LocalDate

class SpaPeriodeService : PeriodeKilde {
    override suspend fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder =
        Sykepengeperioder(aktørId, emptyList())
}