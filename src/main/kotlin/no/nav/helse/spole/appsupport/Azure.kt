package no.nav.helse.spole.appsupport

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.LocalDateTime

@Component
class Azure(val objectMapper: ObjectMapper) {

    @Value("\$AZURE_CLIENT_ID")
    lateinit var clientId: String

    @Value("\$AZURE_CLIENT_SECRET")
    lateinit var clientSecret: String

    @Value("\$AZURE_SCOPE")
    lateinit var scope: String

    @Value("\$AZURE_TOKEN_ENDPOINT")
    lateinit var tokenEndpoint: String

    private var token: Token = Token(tokenType = "not a token", expiresIn = 0, extExpiresIn = 0, accessToken = "not a token")
    private var expiry: LocalDateTime = LocalDateTime.now().minusYears(100)

    fun getToken(): Token {
        if (isExpired()) {
            token = fetchTokenFromAzure()
            expiry = LocalDateTime.now().plusSeconds(token.expiresIn).minusSeconds(60)
        }
        return token
    }

    private fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiry)

    private fun fetchTokenFromAzure(): Token {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val form: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>()
        form.add("client_id", clientId)
        form.add("client_secret", clientSecret)
        form.add("scope", scope)
        form.add("grant_type", "client_credentials")

        val postEntity = HttpEntity(form, headers)
        val response = RestTemplate().postForEntity(tokenEndpoint.asURI(), postEntity, String::class.java)

        return objectMapper.readValue(response.body, Token::class.java)
    }

}

data class Token(val tokenType: String, val expiresIn: Long, val extExpiresIn: Long, val accessToken: String)
private fun String.asURI() = URI(this)