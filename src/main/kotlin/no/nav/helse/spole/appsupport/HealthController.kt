package no.nav.helse.spole.appsupport

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/isalive")
    @ResponseStatus(HttpStatus.OK)
    fun isalive():String = "ALIVE"

    @GetMapping("/isready")
    @ResponseStatus(HttpStatus.OK)
    fun isready():String = "READY"
}