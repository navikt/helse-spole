package no.nav.helse.spole.infotrygd.fnr

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.basic
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import no.nav.helse.spole.JsonConfig
import java.time.LocalDateTime

class StsRestClient(
    val baseUrl: String,
    val username: String,
    val password: String
) {
    private var cachedOidcSTSToken: STSToken? = null

    private val client = HttpClient(Apache) {
        install(JsonFeature) {
            this.serializer = JacksonSerializer { JsonConfig.objectMapper }
        }
        install(Auth) {
            basic {
                username = this@StsRestClient.username
                password = this@StsRestClient.password
            }
        }
    }

    suspend fun token(): String {
        if (STSToken.shouldRenew(cachedOidcSTSToken)) {
            cachedOidcSTSToken =
                client.get<STSToken>("$baseUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid")
        }
        return cachedOidcSTSToken!!.accessToken
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class STSToken(
        @JsonProperty("access_token") val accessToken: String, @JsonProperty("token_type") val type: String, @JsonProperty(
            "expires_in"
        ) val expiresIn: Int
    ) {
        // expire 10 seconds before actual expiry. for great margins.
        val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expiresIn - 10L)

        companion object {
            fun shouldRenew(STSToken: STSToken?): Boolean {
                if (STSToken == null) {
                    return true
                }

                return isExpired(STSToken)
            }

            fun isExpired(STSToken: STSToken): Boolean {
                return STSToken.expirationTime.isBefore(LocalDateTime.now())
            }
        }
    }
}
