package no.nav.helse.spole

import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
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
            assertEquals("ALIVE", response.content)
        }
        with(handleRequest(HttpMethod.Get, "/isready")) {
            assertEquals("READY", response.content)
        }
    }

    private fun Application.testEnv() = (environment.config as MapApplicationConfig).apply {
        // Set here the properties
        put("sts.url", "http://ikke.satt")
        put("sts.password", "so_secret")
        put("sts.username", "so_secret_username")

        put("fnrkilde.url", "http://ikke.satt")

        put("azure.client.id", "client")
        put("azure.client.secret", "secret")
        put("azure.scope", "who_knows")
        put("azure.url", "http://ikke.satt")

        put("infotrygd.url", "http://ikke.satt")
    }

}

