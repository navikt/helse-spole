package no.nav.helse.spole

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HistorikkController {

    @GetMapping("/sykepengeperioder/{aktørid}")
    fun hentPerioder(aktørId: AktørId): Sykepengeperioder = Sykepengeperioder(aktørId, emptyList())
}
