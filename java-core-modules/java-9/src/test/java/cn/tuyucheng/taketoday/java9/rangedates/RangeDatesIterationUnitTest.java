package cn.tuyucheng.taketoday.java9.rangedates;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

class RangeDatesIterationUnitTest {

	@Test
	void givenIterateBetweenDatesJava9_WhenStartDateAsTodayAndEndDateAs10DaysAhead() {
		LocalDate start = LocalDate.now();
		LocalDate end = start.plus(10L, ChronoUnit.DAYS);

		RangeDatesIteration iteration = new RangeDatesIteration();

		iteration.iterateBetweenDatesJava9(start, end);
	}

	@Test
	void givenIterateBetweenDatesJava8_WhenStartDateAsTodayAndEndDateAs10DaysAhead() {
		LocalDate start = LocalDate.now();
		LocalDate end = start.plus(10L, ChronoUnit.DAYS);

		RangeDatesIteration iteration = new RangeDatesIteration();

		iteration.iterateBetweenDatesJava8(start, end);
	}

	@Test
	void givenIterateBetweenDatesJava7_WhenStartDateAsTodayAndEndDateAs10DaysAhead() {
		Calendar today = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
		Date start = calendar.getTime();

		calendar.add(Calendar.DATE, 10);
		Date end = calendar.getTime();

		RangeDatesIteration iteration = new RangeDatesIteration();

		iteration.iterateBetweenDatesJava7(start, end);
	}
}