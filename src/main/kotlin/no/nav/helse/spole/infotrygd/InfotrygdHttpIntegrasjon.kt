package no.nav.helse.spole.infotrygd

import no.nav.helse.spole.appsupport.Azure
import no.nav.helse.spole.historikk.Periode
import no.nav.helse.spole.infotrygd.fnr.Fodselsnummer
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class InfotrygdHttpIntegrasjon(val azure: Azure) : InfotrygdIntegrasjon {
    override fun forFnr(fnr: Fodselsnummer, fom: LocalDate): Collection<Periode> {
        val token = azure.hentToken()

        return emptyList()
    }

}