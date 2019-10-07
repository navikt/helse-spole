package no.nav.helse.spole

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.helse.spole.infotrygd.ITSykepenger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SykmeldingsperiodeParseTest {

    @Test
    fun `Bør kunne parse en tom sykepenge-historikk`() {
        val sykepenger: ITSykepenger = JsonConfig.accessTokenMapper.readValue(tomHistorikk)
        assertNotNull(sykepenger)
        assertEquals(0, sykepenger.sykmeldingsperioder.size)
    }

    @Test
    fun `bør klare å parse en vilkårlig sykemeldingsperioderespons`() {
        val parsedValue = JsonConfig.infotrygdMapper.readValue<ITSykepenger>(historikk)

        assertNotNull(parsedValue)
        assertEquals(3, parsedValue.sykmeldingsperioder.size)
    }
}


val tomHistorikk: String = """
    {
        "sykmeldingsperioder":[]
    }
""".trimIndent()


val historikk = """
    {
	"sykmeldingsperioder": [{
		"ident": 99950123100000,
		"tknr": "0999",
		"seq": 79809498,
		"sykemeldtFom": "2019-05-01",
		"sykemeldtTom": "2019-12-31",
		"grad": "100",
		"slutt": "2020-04-28",
		"erArbeidsgiverPeriode": true,
		"stansAarsakKode": "",
		"stansAarsak": "Ukjent..",
		"unntakAktivitet": "",
		"arbeidsKategoriKode": "01",
		"arbeidsKategori": "Arbeidstaker",
		"arbeidsKategori99": "",
		"erSanksjonBekreftet": "",
		"sanksjonsDager": 0,
		"sykemelder": "ETTERNAVN LEGE",
		"behandlet": "2019-05-01",
		"yrkesskadeArt": "",
		"utbetalingList": [{
			"fom": "2019-05-17",
			"tom": "2019-10-31",
			"utbetalingsGrad": "100",
			"oppgjorsType": "",
			"utbetalt": "2019-09-04",
			"dagsats": 2304.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}, {
			"fom": "2019-11-01",
			"tom": "2019-12-31",
			"utbetalingsGrad": "070",
			"oppgjorsType": "",
			"utbetalt": "2019-09-04",
			"dagsats": 1613.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}],
		"inntektList": [{
			"orgNr": "874625752",
			"sykepengerFom": "2019-05-17",
			"refusjonTom": null,
			"refusjonsType": "J",
			"periodeKode": "M",
			"periode": "Månedlig",
			"loenn": 52000.0
		}],
		"graderingList": [{
			"gradertFom": "2019-05-01",
			"gradertTom": "2019-12-31",
			"grad": "100"
		}],
		"forsikring": []
	}, {
		"ident": 99950123100000,
		"tknr": "0999",
		"seq": 79819869,
		"sykemeldtFom": "2018-01-30",
		"sykemeldtTom": "2018-05-24",
		"grad": "020",
		"slutt": "2019-01-30",
		"erArbeidsgiverPeriode": true,
		"stansAarsakKode": "AA",
		"stansAarsak": "Avsluttet",
		"unntakAktivitet": "",
		"arbeidsKategoriKode": "01",
		"arbeidsKategori": "Arbeidstaker",
		"arbeidsKategori99": "",
		"erSanksjonBekreftet": "",
		"sanksjonsDager": 0,
		"opphoerFom": "2018-05-25",
		"sykemelder": "ETTERNAVN ANNENLEGE",
		"behandlet": "2018-01-30",
		"yrkesskadeArt": "",
		"utbetalingList": [{
			"fom": "2018-05-15",
			"tom": "2018-05-24",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-06-01",
			"dagsats": 432.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}, {
			"fom": "2018-05-14",
			"tom": "2018-05-14",
			"utbetalingsGrad": "",
			"oppgjorsType": "",
			"utbetalt": "2018-06-01",
			"dagsats": 0.0,
			"typeKode": "9",
			"typeTekst": "Ferie"
		}, {
			"fom": "2018-05-01",
			"tom": "2018-05-13",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-06-01",
			"dagsats": 432.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}, {
			"fom": "2018-04-06",
			"tom": "2018-05-24",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-05-02",
			"dagsats": 432.0,
			"typeKode": "7",
			"typeTekst": "Tilbakeført"
		}, {
			"fom": "2018-04-06",
			"tom": "2018-04-30",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-06-01",
			"dagsats": 432.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}, {
			"fom": "2018-03-27",
			"tom": "2018-04-05",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-04-13",
			"dagsats": 432.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}, {
			"fom": "2018-03-26",
			"tom": "2018-03-26",
			"utbetalingsGrad": "",
			"oppgjorsType": "",
			"utbetalt": "2018-04-13",
			"dagsats": 0.0,
			"typeKode": "9",
			"typeTekst": "Ferie"
		}, {
			"fom": "2018-03-19",
			"tom": "2018-03-25",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-04-13",
			"dagsats": 432.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}, {
			"fom": "2018-03-01",
			"tom": "2018-03-18",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-03-26",
			"dagsats": 432.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}, {
			"fom": "2018-02-15",
			"tom": "2018-02-28",
			"utbetalingsGrad": "020",
			"oppgjorsType": "",
			"utbetalt": "2018-03-15",
			"dagsats": 432.0,
			"typeKode": "5",
			"typeTekst": "ArbRef"
		}],
		"inntektList": [{
			"orgNr": "874625752",
			"sykepengerFom": "2018-02-15",
			"refusjonTom": null,
			"refusjonsType": "J",
			"periodeKode": "M",
			"periode": "Månedlig",
			"loenn": 52617.0
		}],
		"graderingList": [{
			"gradertFom": "2018-05-25",
			"gradertTom": "2018-06-30",
			"grad": "020"
		}, {
			"gradertFom": "2018-04-06",
			"gradertTom": "2018-05-24",
			"grad": "020"
		}, {
			"gradertFom": "2018-03-01",
			"gradertTom": "2018-04-05",
			"grad": "020"
		}, {
			"gradertFom": "2018-01-30",
			"gradertTom": "2018-02-28",
			"grad": "020"
		}, {
			"gradertFom": "2018-01-30",
			"gradertTom": "2018-05-24",
			"grad": "020"
		}],
		"forsikring": []
	}, {
		"ident": 99950123100000,
		"tknr": "0999",
		"seq": 79829686,
		"sykemeldtFom": "2017-03-13",
		"sykemeldtTom": "2017-03-22",
		"grad": "100",
		"erArbeidsgiverPeriode": false,
		"stansAarsakKode": "",
		"stansAarsak": "Ukjent..",
		"unntakAktivitet": "",
		"arbeidsKategoriKode": "01",
		"arbeidsKategori": "Arbeidstaker",
		"arbeidsKategori99": "99",
		"erSanksjonBekreftet": "",
		"sanksjonsDager": 0,
		"sykemelder": "ETTERNAVN TREDJELEGE",
		"behandlet": "2017-03-13",
		"yrkesskadeArt": "",
		"utbetalingList": [],
		"inntektList": [],
		"graderingList": [{
			"gradertFom": "2017-03-13",
			"gradertTom": "2017-03-17",
			"grad": "100"
		}, {
			"gradertFom": "2017-03-13",
			"gradertTom": "2017-03-22",
			"grad": "100"
		}],
		"forsikring": []
	}]
}
""".trimIndent()