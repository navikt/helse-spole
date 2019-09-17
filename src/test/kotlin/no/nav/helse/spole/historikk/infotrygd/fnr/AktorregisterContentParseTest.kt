package no.nav.helse.spole.historikk.infotrygd.fnr

import no.nav.helse.spole.infotrygd.fnr.foedselsnummerFraAktørregisterResponse
import org.junit.Test
import java.lang.RuntimeException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AktørregisterContentParseTest {

    @Test
    fun `skal klare å hente ut Norsk Ident fra en én til én response`() {
        val fnr = enTilEn.foedselsnummerFraAktørregisterResponse()
        assertEquals("01010101010", fnr)
    }

    @Test
    fun `men hva skjer med feilmeldinger? Foreløpig pakker vi det inn i en exception`() {
        assertFailsWith<RuntimeException>("Burde ha kastet Exception. Bør kanskje også tenkes på nytt.") { feilmelding.foedselsnummerFraAktørregisterResponse() }
    }

}

val enTilEn: String = """
    {
        "1000000000000": {
            "identer": [
                {
                    "ident": "01010101010",
                    "identgruppe": "NorskIdent",
                    "gjeldende": true
                },
                {
                    "ident": "1000000000000",
                    "identgruppe": "AktoerId",
                    "gjeldende": true
                }
            ],
            "feilmelding": null
        }
    }
""".trimIndent()

val feilmelding = """
    {
        "1000000000000": {
            "identer": null,
            "feilmelding": "Den angitte personidenten finnes ikke"
        }
    }
""".trimIndent()