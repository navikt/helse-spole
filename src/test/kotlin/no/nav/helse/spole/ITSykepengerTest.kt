package no.nav.helse.spole

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.helse.spole.infotrygd.ITSykepenger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ITSykepengerTest {
    @Test
    fun `Bør kunne parse en tom sykepenge-historikk`() {
        val sykepenger: ITSykepenger = JsonConfig.objectMapper.readValue(tomHistorikk)
        assertNotNull(sykepenger)
        assertEquals(0, sykepenger.sykmeldingsperioder.size)
    }
}

val tomHistorikk: String = """
    {
        "sykmeldingsperioder":[]
    }
""".trimIndent()
