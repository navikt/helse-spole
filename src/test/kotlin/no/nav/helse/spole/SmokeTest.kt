package no.nav.helse.spole

import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlin.test.Test
import kotlin.test.assertEquals

// just making sure the thing actually starts.
@KtorExperimentalAPI
class SmokeTest {

    @Test
    fun smoke() = withTestApplication({
        testEnv()
        spole()
    }) {
        with(handleRequest(HttpMethod.Get, "/isalive")) {
            assertEquals(HttpStatusCode.OK, response.status(), "Skal eksponere metrikker")
            assertEquals("ALIVE", response.content, "Skal ha et svar på `isalive`")
        }
        with(handleRequest(HttpMethod.Get, "/isready")) {
            assertEquals(HttpStatusCode.OK, response.status(), "Skal eksponere metrikker")
            assertEquals("READY", response.content, "Skal ha et svar på `isready`")
        }
        with(handleRequest (HttpMethod.Get, "/internal/metrics") ){
            assertEquals(HttpStatusCode.OK, response.status(), "Skal eksponere metrikker")
        }
    }

    private fun Application.testEnv() = (environment.config as MapApplicationConfig).apply {
        put("sts.url", "http://ikke.satt")
        put("sts.password", "so_secret")
        put("sts.username", "so_secret_username")

        put("fnrkilde.url", "http://ikke.satt")

        put("azure.client.id", "client")
        put("azure.client.secret", "secret")
        put("azure.scope", "who_knows")
        put("azure.tenant.id", "some_long_string")

        put("infotrygd.url", "http://ikke.satt")
        put("infotrygd.timeout", "1000000")

        put("jwt.audience", "blandt de beste i hollywood")
        put("jwt.realm", "a magic kingdom beyond the hills")
    }

}

