package no.nav.helse.spole.historikk.infotrygd.fnr

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondBytes
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.toUtf8Bytes
import no.nav.helse.spole.JsonConfig
import no.nav.helse.spole.infotrygd.fnr.StsRestClient
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertNotNull

class BasicAuthTest {

    val stsClient = StsRestClient(baseUrl = "http://localhost", password = "testPass", username = "testUser")

    @Test
    fun shouldHaveAuthHeaders(): Unit {
        runServerAndCheckCredentials()
        runBlocking {
            val toke = stsClient.token()
            assertNotNull(toke)
        }
    }

    fun runServerAndCheckCredentials() {
        embeddedServer(Netty, 80) {
            install(Authentication) {
                basic("aname") {
                    realm = "somerealm"
                    validate { credentials ->
                        if (credentials.name == "testUser" && credentials.password == "testPass") UserIdPrincipal(
                            credentials.name
                        ) else null
                    }
                }
            }

            routing {
                authenticate("aname") {
                    get("/rest/v1/sts/token") {
                        call.respondBytes(contentType = ContentType.Application.Json, status = HttpStatusCode.OK) {
                            JsonConfig.objectMapper.writeValueAsString(
                                StsRestClient.STSToken(
                                    accessToken = "yup",
                                    expiresIn = 30023,
                                    type = "a token-type token"
                                )
                            ).toUtf8Bytes()
                        }
                    }
                }

            }
        }.start()
    }

}