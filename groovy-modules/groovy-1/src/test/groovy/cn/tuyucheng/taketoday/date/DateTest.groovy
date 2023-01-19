package cn.tuyucheng.taketoday.date

import org.junit.Test

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month

import static org.junit.Assert.assertEquals


class DateTest {

	def dateStr = "2019-02-28"
	def pattern = "yyyy-MM-dd"

	@Test
	void whenGetStringRepresentation_thenCorrectlyConvertIntoDate() {
		def dateFormat = new SimpleDateFormat(pattern)
		def date = dateFormat.parse(dateStr)

		println("       String to Date with DateFormatter : " + date)

		def cal = new GregorianCalendar();
		cal.setTime(date);

		assertEquals(cal.get(Calendar.YEAR), 2019)
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), 28)
		assertEquals(cal.get(Calendar.MONTH), Calendar.FEBRUARY)
	}

	@Test
	void whenGetStringRepresentation_thenCorrectlyConvertWithDateUtilsExtension() {

		def date = Date.parse(pattern, dateStr)

		println("       String to Date with Date.parse : " + date)

		def cal = new GregorianCalendar();
		cal.setTime(date);

		assertEquals(cal.get(Calendar.YEAR), 2019)
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), 28)
		assertEquals(cal.get(Calendar.MONTH), Calendar.FEBRUARY)
	}

	@Test
	void whenGetStringRepresentation_thenCorrectlyConvertIntoDateWithLocalDate() {
		def date = LocalDate.parse(dateStr, pattern)

		println("       String to Date with LocalDate     : " + date)

		assertEquals(date.getYear(), 2019)
		assertEquals(date.getMonth(), Month.FEBRUARY)
		assertEquals(date.getDayOfMonth(), 28)
	}
}