package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.Fodselsnummer
import no.nav.helse.spole.historikk.AktørId
import no.nav.helse.spole.historikk.Periode
import no.nav.helse.spole.historikk.PeriodeKilde
import no.nav.helse.spole.historikk.Sykepengeperioder
import java.time.LocalDate

class InfotrygdPeriodeService(val fnrMapper: AktørTilFnrMapper, val infotrygd: InfotrygdIntegrasjon) : PeriodeKilde {

    override fun perioder(aktørId: AktørId, fom: LocalDate, tom: LocalDate): Sykepengeperioder {
        return Sykepengeperioder(aktørId, infotrygd.forFnr(fnrMapper.tilFnr(aktørId), fom, tom))
    }
}

interface AktørTilFnrMapper {
    fun tilFnr(aktørId: String): Fodselsnummer
}

interface InfotrygdIntegrasjon {
    fun forFnr(fnr: Fodselsnummer, fom: LocalDate, tom: LocalDate): Collection<Periode>
}
