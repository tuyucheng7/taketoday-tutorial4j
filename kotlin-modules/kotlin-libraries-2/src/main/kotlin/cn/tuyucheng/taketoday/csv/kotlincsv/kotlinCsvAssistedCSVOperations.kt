package cn.tuyucheng.taketoday.csv.kotlincsv

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.math.BigDecimal

fun readRelaxedCsv(inputStream: InputStream): List<cn.tuyucheng.taketoday.csv.model.TaxableGood> = csvReader {
	skipEmptyLine = true
	escapeChar = '\\'
}.open(inputStream) {
	readAllWithHeaderAsSequence().map {
		cn.tuyucheng.taketoday.csv.model.TaxableGood(
			it["Index"]!!.trim().toInt(),
			it.k("Item")!!.trim(),
			BigDecimal(it.k("Cost")),
			BigDecimal(it.k("Tax")),
			BigDecimal(it.k("Total"))
		)
	}.toList()
}

fun readStrictCsv(inputStream: InputStream): List<cn.tuyucheng.taketoday.csv.model.TaxableGood> =
	csvReader().open(inputStream) {
		readAllWithHeaderAsSequence().map {
			cn.tuyucheng.taketoday.csv.model.TaxableGood(
				it["Index"]!!.trim().toInt(),
				it["Item"]!!.trim(),
				BigDecimal(it["Cost"]),
				BigDecimal(it["Tax"]),
				BigDecimal(it["Total"])
			)
		}.toList()
	}

internal fun Map<String, String>.k(key: String) = this[keys.find { it.contains(key) }]?.trim()