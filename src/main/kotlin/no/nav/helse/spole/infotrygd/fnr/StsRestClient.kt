package no.nav.helse.spole.infotrygd.fnr

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.basic
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import java.net.URI
import java.time.LocalDateTime

class StsRestClient(val stsRestUrl: URI,
                    val stsRestUsername: String,
                    val stsRestPassword: String) {
    private var cachedOidcToken: Token? = null
    private val client = HttpClient(Apache) {
        install(Auth) {
            basic {
                username = stsRestUsername
                password = stsRestPassword
            }
        }
    }

    suspend fun token(): String {
        if (Token.shouldRenew(cachedOidcToken))  {
            cachedOidcToken = client.request<Token> {
                url("${stsRestUrl.toString()}/rest/v1/sts/token?grant_type=client_credentials&scope=openid")
                method = HttpMethod.Get
            }
        }
        return cachedOidcToken!!.accessToken
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class Token(@JsonProperty("access_token") val accessToken: String, @JsonProperty("token_type") val type: String, @JsonProperty("expires_in") val expiresIn: Int) {
        // expire 10 seconds before actual expiry. for great margins.
        val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expiresIn - 10L)

        companion object {
            fun shouldRenew(token: Token?): Boolean {
                if (token == null) {
                    return true
                }

                return isExpired(token)
            }

            fun isExpired(token: Token): Boolean {
                return token.expirationTime.isBefore(LocalDateTime.now())
            }
        }
    }
}
