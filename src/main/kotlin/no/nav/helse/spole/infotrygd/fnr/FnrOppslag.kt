package no.nav.helse.spole.infotrygd.fnr

import no.nav.helse.spole.infotrygd.AktørTilFnrMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
@Profile(value=["preprod", "prod"])
class FnrOppslag(val stsRestClient: StsRestClient,
                 @Value("\${services.sparkel.url}") val sparkelBaseUrl: String): AktørTilFnrMapper {

    override fun tilFnr(aktørId: String): Fodselsnummer {
        val bearer = stsRestClient.token()
        val webClient = WebClient.builder().baseUrl(sparkelBaseUrl).build()
        return webClient.get()
                .uri("/api/aktor/$aktørId/fnr")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $bearer")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
                .header("Nav-Consumer-Id", "spenn")
                .retrieve()
                .bodyToMono(Fodselsnummer::class.java).block()!!
    }

}

@Component
@Profile(value=["test", "default", "integration"])
class DummyAktørMapper() : AktørTilFnrMapper {
    override fun tilFnr(aktørId: String): Fodselsnummer = aktørId
}

typealias Fodselsnummer = String
