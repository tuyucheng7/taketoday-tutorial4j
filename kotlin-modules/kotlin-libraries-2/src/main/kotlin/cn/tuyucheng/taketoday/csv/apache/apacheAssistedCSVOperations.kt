package cn.tuyucheng.taketoday.csv.apache

import org.apache.commons.csv.CSVFormat
import java.io.InputStream
import java.io.Writer
import java.math.BigDecimal

fun readCsv(inputStream: InputStream): List<cn.tuyucheng.taketoday.csv.model.TaxableGood> =
	CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
		setIgnoreSurroundingSpaces(true)
	}.build().parse(inputStream.reader())
		.drop(1) // Dropping the header
		.map {
			cn.tuyucheng.taketoday.csv.model.TaxableGood(
				index = it[0].toInt(),
				item = it[1],
				cost = BigDecimal(it[2]),
				tax = BigDecimal(it[3]),
				total = BigDecimal(it[4])
			)
		}

fun Writer.writeCsv(goods: List<cn.tuyucheng.taketoday.csv.model.TaxableGood>) {
	CSVFormat.DEFAULT.print(this).apply {
		printRecord("Index", "Item", "Cost", "Tax", "Total")
		goods.forEach { (index, item, cost, tax, total) -> printRecord(index, item, cost, tax, total) }
	}
}