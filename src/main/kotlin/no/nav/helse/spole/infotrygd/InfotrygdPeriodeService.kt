package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.historikk.AktørId
import no.nav.helse.spole.historikk.Periode
import no.nav.helse.spole.historikk.PeriodeKilde
import no.nav.helse.spole.historikk.Sykepengeperioder
import no.nav.helse.spole.infotrygd.fnr.Fodselsnummer
import java.time.LocalDate

class InfotrygdPeriodeService(val fnrMapper: AktørTilFnrMapper, val infotrygd: InfotrygdIntegrasjon) : PeriodeKilde {

    override suspend fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder {
        return Sykepengeperioder(aktørId, infotrygd.forFnr(fnrMapper.tilFnr(aktørId), fom))
    }
}

interface AktørTilFnrMapper {
    suspend fun tilFnr(aktørId: String): Fodselsnummer
}

interface InfotrygdIntegrasjon {
    suspend fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode>
}