package no.nav.helse.spole.appsupport

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.forms.formData
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import java.net.URI
import java.time.LocalDateTime

class Azure(
    val objectMapper: ObjectMapper,
    val clientId: String,
    val clientSecret: String,
    val scope: String,
    val endpoint: URI
) {

    private var client = HttpClient(Apache) {
        install(JsonFeature) {
            this.serializer = JacksonSerializer { objectMapper }
        }
    }

    private var token: Token =
        Token(tokenType = "not a token", expiresIn = 0, extExpiresIn = 0, accessToken = "not a token")
    private var expiry: LocalDateTime = LocalDateTime.now().minusYears(100)

    suspend fun hentToken(): Token {
        if (isExpired()) {
            token = fetchTokenFromAzure()
            expiry = LocalDateTime.now().plusSeconds(token.expiresIn).minusSeconds(60)
        }
        return token
    }

    private fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiry)

    private suspend fun fetchTokenFromAzure(): Token = client.request {
        url(endpoint.toString())
        method = HttpMethod.Post
        headers["ContentType"] = "application/x-www-form-urlencoded"
        formData {
            append("client_id", clientId)
            append("client_secret", clientSecret)
            append("scope", scope)
            append("grant_type", "client_credentials")
        }
    }
}

data class Token(val tokenType: String, val expiresIn: Long, val extExpiresIn: Long, val accessToken: String)