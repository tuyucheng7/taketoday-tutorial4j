package cn.tuyucheng.taketoday.java14.newfeatues;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertTrue;

public class SwitchExprUnitTest {

	@Test
	public void givenDay_whenSunday_thenWeekend() {
		assertTrue(isTodayHolidayInJava8("SUNDAY"));

		assertTrue(isTodayHolidayInJava14("SUNDAY"));

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> isTodayHolidayInJava8("SOMEDAY"));

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> isTodayHolidayInJava14("SOMEDAY"));
	}

	private boolean isTodayHolidayInJava8(String day) {

		boolean isTodayHoliday;
		switch (day) {
			case "MONDAY":
			case "TUESDAY":
			case "WEDNESDAY":
			case "THURSDAY":
			case "FRIDAY":
				isTodayHoliday = false;
				break;
			case "SATURDAY":
			case "SUNDAY":
				isTodayHoliday = true;
				break;
			default:
				throw new IllegalArgumentException("What's a " + day);
		}
		return isTodayHoliday;
	}

	private boolean isTodayHolidayInJava14(String day) {
		return switch (day) {
			case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> false;
			case "SATURDAY", "SUNDAY" -> true;
			default -> throw new IllegalArgumentException("What's a " + day);
		};
	}
}