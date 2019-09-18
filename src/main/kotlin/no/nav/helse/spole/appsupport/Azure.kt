package no.nav.helse.spole.appsupport

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.httpPost
import no.nav.helse.spole.JsonConfig
import java.net.URI
import java.time.LocalDateTime

class Azure(
    val clientId: String,
    val clientSecret: String,
    val scope: String,
    val tenantId: String
) {
    private var token: Token =
        Token(tokenType = "not a token", expiresIn = 0, extExpiresIn = 0, accessToken = "not a token")
    private var expiry: LocalDateTime = LocalDateTime.now().minusYears(100)

    fun hentToken(): Token {
        println("henter azure ad token")
        if (isExpired()) {
            println("autentiserer mot azure ad")
            token = fetchTokenFromAzure()
            expiry = LocalDateTime.now().plusSeconds(token.expiresIn).minusSeconds(60)
        }
        println("returnerer azure ad token")
        return token
    }

    private fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiry)

    private fun fetchTokenFromAzure(): Token {
        val (_, _, result) = "https://login.microsoftonline.com/$tenantId/oauth2/v2.0/token".httpPost(
            listOf(
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "scope" to scope,
                "grant_type" to "client_credentials"
            )
        ).response()
        return JsonConfig.objectMapper.readValue(result.get())
    }
}

data class Token(val tokenType: String, val expiresIn: Long, val extExpiresIn: Long, val accessToken: String)