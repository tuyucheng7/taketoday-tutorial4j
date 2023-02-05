package cn.tuyucheng.taketoday.internationalization;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateFormatUnitTest {

	static void main(String[] args) {

	}

	@Test
	void givenGregorianCalendar_whenLocaleSpecificDateInstance_givenLanguageSpecificMonths() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2018, 1, 1, 10, 15, 20);
		Date date = gregorianCalendar.getTime();

		DateFormat itInstance = DateFormat.getDateInstance(DateFormat.FULL, Locale.ITALY);
		DateFormat usInstance = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);

		assertEquals("giovedì 1 febbraio 2018", itInstance.format(date));
		assertEquals("Thursday, February 1, 2018", usInstance.format(date));
	}

	@Test
	void givenGregorianCalendar_whenDateInstanceWithDifferentFormats_givenSpecificDateFormatting() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2018, 1, 1, 10, 15, 20);
		Date date = gregorianCalendar.getTime();

		DateFormat fullInstance = DateFormat.getDateInstance(DateFormat.FULL, Locale.ITALY);
		DateFormat mediumInstance = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ITALY);

		assertEquals("giovedì 1 febbraio 2018", fullInstance.format(date));
		assertEquals("1-feb-2018", mediumInstance.format(date));
	}

	@Test
	void givenGregorianCalendar_whenTimeInstanceWithDifferentFormats_givenSpecificTimeFormatting() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2018, 1, 1, 10, 15, 20);
		gregorianCalendar.setTimeZone(TimeZone.getTimeZone("CET"));
		TimeZone.setDefault(TimeZone.getTimeZone("CET"));
		Date date = gregorianCalendar.getTime();

		DateFormat fullInstance = DateFormat.getTimeInstance(DateFormat.FULL, Locale.ITALY);
		DateFormat mediumInstance = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.ITALY);

		assertEquals("10.15.20 CET", fullInstance.format(date));
		assertEquals("10.15.20", mediumInstance.format(date));
	}

	@Test
	void givenGregorianCalendar_whenDateTimeInstanceWithDifferentFormats_givenSpecificDateTimeFormatting() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2018, 1, 1, 10, 15, 20);
		gregorianCalendar.setTimeZone(TimeZone.getTimeZone("CET"));
		TimeZone.setDefault(TimeZone.getTimeZone("CET"));
		Date date = gregorianCalendar.getTime();

		DateFormat ffInstance = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.ITALY);
		DateFormat smInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.ITALY);

		assertEquals("giovedì 1 febbraio 2018 10.15.20 CET", ffInstance.format(date));
		assertEquals("01/02/18 10.15.20", smInstance.format(date));
	}

	@Test
	void givenGregorianCalendar_whenLocaleSpecificDateTimeInstance_givenLocaleSpecificFormatting() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2018, 1, 1, 10, 15, 20);
		gregorianCalendar.setTimeZone(TimeZone.getTimeZone("CET"));
		TimeZone.setDefault(TimeZone.getTimeZone("CET"));
		Date date = gregorianCalendar.getTime();

		DateFormat itInstance = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.ITALY);
		DateFormat jpInstance = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.JAPAN);

		assertEquals("giovedì 1 febbraio 2018 10.15.20 CET", itInstance.format(date));
		assertEquals("2018年2月1日 10時15分20秒 CET", jpInstance.format(date));
	}

	@Test
	void givenGregorianCalendar_whenCustomizedSimpleDateFormat_thenSpecificMonthRepresentations() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2018, 1, 1, 10, 15, 20);
		Date date = gregorianCalendar.getTime();
		Locale.setDefault(new Locale("pl", "PL"));

		SimpleDateFormat fullMonthDateFormat = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss:SSS");
		SimpleDateFormat shortMonthsimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");

		assertEquals("01-lutego-2018 10:15:20:000", fullMonthDateFormat.format(date));
		assertEquals("01-02-2018 10:15:20:000", shortMonthsimpleDateFormat.format(date));
	}

	@Test
	void givenGregorianCalendar_whenCustomizedDateFormatSymbols_thenChangedDayNames() {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(2018, 1, 1, 10, 15, 20);
		Date date = gregorianCalendar.getTime();
		Locale.setDefault(new Locale("pl", "PL"));

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
		dateFormatSymbols.setWeekdays(new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});
		SimpleDateFormat standardDateFormat = new SimpleDateFormat("EEEE-MMMM-yyyy HH:mm:ss:SSS");
		SimpleDateFormat newDaysDateFormat = new SimpleDateFormat("EEEE-MMMM-yyyy HH:mm:ss:SSS", dateFormatSymbols);

		assertEquals("czwartek-lutego-2018 10:15:20:000", standardDateFormat.format(date));
		assertEquals("F-lutego-2018 10:15:20:000", newDaysDateFormat.format(date));
	}
}