package no.nav.helse.spole.historikk

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.Routing
import no.nav.helse.spole.AUTH_NAME
import no.nav.helse.spole.JsonConfig
import java.time.LocalDate

class HistorikkTjeneste(val infotrygd: PeriodeKilde,
                        val spa: PeriodeKilde) {

    fun hentPerioder(aktørId: AktørId,
                     fom: LocalDate?): Sykepengeperioder {
        val faktiskFom = fom ?: LocalDate.now().minusYears(3)
        return infotrygdPerioder(aktørId, faktiskFom).join(spaPerioder(aktørId, faktiskFom))
    }

    private fun infotrygdPerioder(aktørId: AktørId, fom: LocalDate) = infotrygd.perioder(aktørId, fom)
    private fun spaPerioder(aktørId: AktørId, fom: LocalDate) = spa.perioder(aktørId, fom)
}

interface PeriodeKilde {
    fun perioder(aktørId: AktørId, fom: LocalDate): Sykepengeperioder
}

fun Routing.historikk(tjeneste: HistorikkTjeneste) {
    authenticate(AUTH_NAME) {
        get("/sykepengeperioder/{aktorId}") {
            val perioder =
                tjeneste.hentPerioder(call.parameters["aktorId"]!!, LocalDate.now().minusYears(3))
            call.respond(HttpStatusCode.OK, JsonConfig.accessTokenMapper.writeValueAsBytes(perioder))
        }
    }
}