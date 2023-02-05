package cn.tuyucheng.taketoday.localdate;

import org.junit.Test;

import java.time.Month;

import static org.junit.Assert.assertEquals;

public class LocalDateExampleUnitTest {
	private LocalDateExample date = new LocalDateExample();

	@Test
	public void givenValues_whenUsingOfMethod_thenLocalDate() {
		assertEquals("2020-01-08", date.getCustomDateOne(2020, 1, 8).toString());
	}

	@Test
	public void givenValuesWithMonthEnum_whenUsingOfMethod_thenLocalDate() {
		assertEquals("2020-01-08", date.getCustomDateTwo(2020, Month.JANUARY, 8).toString());
	}

	@Test
	public void givenValues_whenUsingEpochDay_thenLocalDate() {
		assertEquals("2020-01-08", date.getDateFromEpochDay(18269).toString());
	}

	@Test
	public void givenValues_whenUsingYearDay_thenLocalDate() {
		assertEquals("2020-01-08", date.getDateFromYearAndDayOfYear(2020, 8).toString());
	}

	@Test
	public void givenValues_whenUsingParse_thenLocalDate() {
		assertEquals("2020-01-08", date.getDateFromString("2020-01-08").toString());
	}

	@Test
	public void givenValuesWithFormatter_whenUsingParse_thenLocalDate() {
		assertEquals("2020-01-08", date.getDateFromStringAndFormatter("8-Jan-2020", "d-MMM-yyyy").toString());
	}
}