package no.nav.helse.spole.infotrygd.fnr

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import no.nav.helse.spole.JsonConfig
import java.time.LocalDateTime

class StsRestClient(
    val baseUrl: String,
    val username: String,
    val password: String
) {
    private var cachedOidcSTSToken: STSToken? = null

    fun token(): String {
        if (STSToken.shouldRenew(cachedOidcSTSToken)) {
            val (_, _, result) = "$baseUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid".httpGet()
                .authentication()
                .basic(username, password)
                .header(mapOf("Accept" to "application/json"))
                .response()

            cachedOidcSTSToken =
                JsonConfig.accessTokenMapper.readValue(result.get(), STSToken::class.java)
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
