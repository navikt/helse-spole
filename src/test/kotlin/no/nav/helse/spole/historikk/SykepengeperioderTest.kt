package no.nav.helse.spole.historikk

import org.junit.Test
import java.lang.RuntimeException
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SykepengeperioderTest {

    @Test
    fun `Ikke slå sammen sykepengeperioder med forskjellig aktørId`() {
        val perioderA = Sykepengeperioder("a", emptyList())
        val perioderB = Sykepengeperioder("b", emptyList())

        assertFailsWith<RuntimeException> { perioderA.join(perioderB) }
    }

    @Test
    fun `Tomme perioder slås sammen til én tom periode`() {
        val perioderA = Sykepengeperioder("a", emptyList())
        val perioderB = Sykepengeperioder("a", emptyList())

        val kombinert = perioderA.join(perioderB)
        assertEquals("a", kombinert.aktørId)
        assertEquals(emptyList(), kombinert.perioder)
    }

    @Test
    fun `Behold høyre siden, når venstre er tom`() {
        val perioderA = Sykepengeperioder("a", listOf(Periode(LocalDate.now(), LocalDate.now(), "100", Kilde.INFOTRYGD)))
        val perioderB = Sykepengeperioder("a", emptyList())

        val kombinert = perioderA.join(perioderB)
        assertEquals(1, kombinert.perioder.size)
    }

    @Test
    fun `Behold venstre siden, når høyre er tom`() {
        val perioderA = Sykepengeperioder("a", listOf(Periode(LocalDate.now(), LocalDate.now(), "100", Kilde.INFOTRYGD)))
        val perioderB = Sykepengeperioder("a", emptyList())

        val kombinert = perioderB.join(perioderA)
        assertEquals(1, kombinert.perioder.size)
    }

    @Test
    fun `Når begge sider har disjunkte perioder, så skal alle være med`() {
        val template = Periode(fom = LocalDate.now(), tom = LocalDate.now(), grad = "100", kilde = Kilde.INFOTRYGD)
        val infoTrygdPeriode1 = template.copy(fom = template.fom.minusDays(160), tom = template.tom.minusDays(140))
        val infoTrygdPeriode2 = template.copy(fom = template.fom.minusDays(130), tom = template.tom.minusDays(110))
        val spaPeriode1 = template.copy(fom = template.fom.minusDays(60), tom = template.tom.minusDays(40), kilde = Kilde.SPA)
        val spaPeriode2 = template.copy(fom = template.fom.minusDays(30), tom = template.tom.minusDays(10), kilde = Kilde.SPA)

        val infotrygd = Sykepengeperioder("a", listOf(infoTrygdPeriode1, infoTrygdPeriode2))
        val spa = Sykepengeperioder("a", listOf(spaPeriode1, spaPeriode2))

        val kombinert = infotrygd.join(spa)
        assertEquals(4, kombinert.perioder.size)
    }
}