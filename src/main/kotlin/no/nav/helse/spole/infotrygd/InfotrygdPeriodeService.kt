package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.historikk.AktørId
import no.nav.helse.spole.historikk.PeriodeKilde
import no.nav.helse.spole.historikk.Sykepengeperioder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Qualifier("infotrygd")
class InfotrygdPeriodeService : PeriodeKilde {
    override fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder =
        Sykepengeperioder(aktørId, emptyList())
}