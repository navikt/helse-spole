package no.nav.helse.spole.historikk

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import no.nav.helse.spole.AUTH_NAME
import java.time.LocalDate

class HistorikkTjeneste(
    val infotrygd: PeriodeKilde,
    val spa: PeriodeKilde
) {

    fun hentPerioder(
        aktørId: AktørId,
        fom: LocalDate = LocalDate.now().minusYears(3),
        tom: LocalDate = LocalDate.now()
    ): Sykepengeperioder {
        return infotrygdPerioder(aktørId, fom, tom).join(spaPerioder(aktørId, fom, tom))
    }

    private fun infotrygdPerioder(aktørId: AktørId, fom: LocalDate, tom: LocalDate) = infotrygd.perioder(aktørId, fom, tom)
    private fun spaPerioder(aktørId: AktørId, fom: LocalDate, tom: LocalDate) = spa.perioder(aktørId, fom, tom)
}

interface PeriodeKilde {
    fun perioder(aktørId: AktørId, fom: LocalDate, tom: LocalDate): Sykepengeperioder
}

fun Routing.historikk(tjeneste: HistorikkTjeneste, mapper: ObjectMapper) {
    authenticate(AUTH_NAME) {
        get("/sykepengeperioder/{aktorId}") {
            val fraDato = call.request.queryParameters["periodeFom"]?.toDate() ?: LocalDate.now().minusYears(3)
            val tilDato = call.request.queryParameters["periodeTom"]?.toDate() ?: LocalDate.now()
            val perioder = tjeneste.hentPerioder(call.parameters["aktorId"]!!, fraDato, tilDato)

            call.respond(HttpStatusCode.OK, mapper.writeValueAsBytes(perioder))
        }
    }
}

private fun String.toDate(): LocalDate = LocalDate.parse(this)
