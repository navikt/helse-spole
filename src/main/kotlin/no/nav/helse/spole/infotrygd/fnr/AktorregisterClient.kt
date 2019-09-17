package no.nav.helse.spole.infotrygd.fnr

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import no.nav.helse.spole.Fodselsnummer
import no.nav.helse.spole.JsonConfig
import no.nav.helse.spole.infotrygd.AktørTilFnrMapper
import java.lang.RuntimeException
import java.net.URI

class AktorregisterClient(
    val sts: StsRestClient,
    val aktørregisterUrl: URI
) : AktørTilFnrMapper {
    override suspend fun tilFnr(aktørId: String): Fodselsnummer {
        println("henter sts-token for fnr-oppslag")
        val token = sts.token()
        println("henter fødselsnummer")
        val (_, _, result) = "$aktørregisterUrl/api/v1/identer?gjeldende=true".httpGet()
            .authentication()
            .bearer(token)
            .header(
                mapOf(
                    "Accept" to "application/json",
                    "Nav-Call-Id" to "spole",
                    "Nav-Consumer-Id" to "spole",
                    "Nav-Personidenter" to aktørId
                )
            )
            .responseString()
        println("parser fødselsnummer")
        return result.get().foedselsnummerFraAktørregisterResponse()
    }

}

fun String.foedselsnummerFraAktørregisterResponse(): Fodselsnummer {
    val rootNode = JsonConfig.objectMapper.readTree(this)
    var identer = emptyList<Ident>()

    rootNode.elements().forEachRemaining {
        val parsed = JsonConfig.objectMapper.treeToValue(it, IdentResultat::class.java)
        if (parsed.feilmelding != null) throw RuntimeException("Feil i fnr oppslag: ${parsed.feilmelding}")
        else identer = identer.plus(parsed.identer as Iterable<Ident>)
    }

    return identer.find {
        it.gjeldende && "NorskIdent".equals(it.identgruppe)
    }!!.ident
}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class Ident(val ident: String, val identgruppe: String, val gjeldende: Boolean)
@JsonIgnoreProperties(ignoreUnknown = true)
private data class IdentResultat(val identer: List<Ident>?, val feilmelding: String?)