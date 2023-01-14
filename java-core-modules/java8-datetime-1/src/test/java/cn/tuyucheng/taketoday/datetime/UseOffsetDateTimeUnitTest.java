package cn.tuyucheng.taketoday.datetime;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class UseOffsetDateTimeUnitTest {
	private final UseOffsetDateTime subject = new UseOffsetDateTime();

	@Test
	public void givenAZoneOffSetAndLocalDateTime_whenCombing_thenValidResult() {
		ZoneOffset offset = ZoneOffset.of("+02:00");
		LocalDateTime localDateTime = LocalDateTime.of(2015, Month.FEBRUARY, 20, 6, 30);

		OffsetDateTime result = subject.offsetOfLocalDateTimeAndOffset(localDateTime, offset);

		assertThat(result.toString()).isEqualTo("2015-02-20T06:30+02:00");
	}
}