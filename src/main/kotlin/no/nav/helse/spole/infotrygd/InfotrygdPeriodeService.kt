package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.historikk.AktørId
import no.nav.helse.spole.historikk.Periode
import no.nav.helse.spole.historikk.PeriodeKilde
import no.nav.helse.spole.historikk.Sykepengeperioder
import no.nav.helse.spole.infotrygd.fnr.Fodselsnummer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Qualifier("infotrygd")
class InfotrygdPeriodeService(val fnrMapper: AktørTilFnrMapper, val infotrygd: InfotrygdIntegrasjon) : PeriodeKilde {

    override fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder {
        return Sykepengeperioder(aktørId, infotrygd.forFnr(fnrMapper.tilFnr(aktørId), fom))
    }
}

interface AktørTilFnrMapper {
    fun tilFnr(aktørId: String): Fodselsnummer
}

interface InfotrygdIntegrasjon {
    fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode>
}