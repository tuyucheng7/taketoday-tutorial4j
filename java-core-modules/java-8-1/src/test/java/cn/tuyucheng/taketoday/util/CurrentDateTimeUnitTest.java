package cn.tuyucheng.taketoday.util;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrentDateTimeUnitTest {

	private static final Clock clock = Clock.fixed(Instant.parse("2016-10-09T15:10:30.00Z"), ZoneId.of("UTC"));

	@Test
	void shouldReturnCurrentDate() {
		final LocalDate now = LocalDate.now(clock);

		assertEquals(9, now.get(ChronoField.DAY_OF_MONTH));
		assertEquals(10, now.get(ChronoField.MONTH_OF_YEAR));
		assertEquals(2016, now.get(ChronoField.YEAR));
	}

	@Test
	void shouldReturnCurrentTime() {
		final LocalTime now = LocalTime.now(clock);

		assertEquals(15, now.get(ChronoField.HOUR_OF_DAY));
		assertEquals(10, now.get(ChronoField.MINUTE_OF_HOUR));
		assertEquals(30, now.get(ChronoField.SECOND_OF_MINUTE));
	}

	@Test
	void shouldReturnCurrentTimestamp() {
		final Instant now = Instant.now(clock);

		assertEquals(clock.instant().getEpochSecond(), now.getEpochSecond());
	}
}