package no.nav.helse.spole.appsupport

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthStatusController {

    @GetMapping("/isalive")
    fun isAlive(): ResponseEntity<String> = ResponseEntity.ok("ALIVE")

    @GetMapping("/isready")
    fun isReady(): ResponseEntity<String> = ResponseEntity.ok("READY")
}