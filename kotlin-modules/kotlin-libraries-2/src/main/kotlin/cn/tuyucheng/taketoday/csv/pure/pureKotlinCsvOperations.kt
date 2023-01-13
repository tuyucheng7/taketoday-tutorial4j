package cn.tuyucheng.taketoday.csv.pure

import java.io.InputStream
import java.io.OutputStream
import java.time.Year

fun readCsv(inputStream: InputStream): List<cn.tuyucheng.taketoday.csv.model.Movie> {
	val reader = inputStream.bufferedReader()
	val header = reader.readLine()
	return reader.lineSequence()
		.filter { it.isNotBlank() }
		.map {
			val (year, rating, title) = it.split(',', ignoreCase = false, limit = 3)
			cn.tuyucheng.taketoday.csv.model.Movie(
				Year.of(year.trim().toInt()),
				rating.trim().toInt(),
				title.trim().removeSurrounding("\"")
			)
		}.toList()
}

fun OutputStream.writeCsv(movies: List<cn.tuyucheng.taketoday.csv.model.Movie>) {
	val writer = bufferedWriter()
	writer.write(""""Year", "Score", "Title"""")
	writer.newLine()
	movies.forEach {
		writer.write("${it.year}, ${it.score}, \"${it.title}\"")
		writer.newLine()
	}
	writer.flush()
}
