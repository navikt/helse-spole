package no.nav.helse.spole.historikk

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.helse.spole.JsonConfig
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class SykepengeperioderJsonTest {

    @Test
    fun `objekt til JSON og tilbake til object`() {
        val json = JsonConfig.accessTokenMapper.writeValueAsBytes(perioder)
        val objekt: Sykepengeperioder = JsonConfig.accessTokenMapper.readValue(json)

        assertEquals("10000000000", objekt.aktørId)
        assertEquals(2, objekt.perioder.size)
    }

}

val perioder = Sykepengeperioder(
    aktørId = "10000000000", perioder = listOf(
        Periode(
            fom = LocalDate.now().minusYears(5),
            tom = LocalDate.now().minusYears(5).plusWeeks(6),
            sykemeldingsgrad = "60.5",
            utbetalingsgrad = "60.5",
            kilde = Kilde.INFOTRYGD
        ),
        Periode(
            fom = LocalDate.now().minusYears(1),
            tom = LocalDate.now().minusYears(1).plusWeeks(6),
            sykemeldingsgrad = "100",
            utbetalingsgrad = "100",
            kilde = Kilde.SPA
        )
    )
)