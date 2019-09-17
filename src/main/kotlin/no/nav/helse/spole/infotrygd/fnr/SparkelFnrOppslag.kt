package no.nav.helse.spole.infotrygd.fnr

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import no.nav.helse.spole.infotrygd.AktørTilFnrMapper
import java.net.URI
import java.util.*

class SparkelFnrOppslag(
    val sts: StsRestClient,
    val sparkelBaseUrl: URI
) : AktørTilFnrMapper {

    private val client = HttpClient(Apache)

    override suspend fun tilFnr(aktørId: String): Fodselsnummer = client.request<Fodselsnummer> {
        url("${sparkelBaseUrl.toString()}/api/aktor/${aktørId}/fnr")
        headers["Authorization"] = "Bearer ${sts.token()}"
        headers["Nav-Call-Id"] = UUID.randomUUID().toString()
        headers["Nav-Consumer-Id"] = UUID.randomUUID().toString()
    }
}

class DummyAktørMapper() : AktørTilFnrMapper {
    override suspend fun tilFnr(aktørId: String): Fodselsnummer = aktørId
}

typealias Fodselsnummer = String
