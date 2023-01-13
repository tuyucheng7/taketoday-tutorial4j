package cn.tuyucheng.taketoday.hamcrest;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class HamcrestNumberUnitTest {

	@Test
	void givenADouble_whenCloseTo_thenCorrect() {
		double actual = 1.3;
		double operand = 1;
		double error = 0.5;
		assertThat(actual, is(closeTo(operand, error)));
	}

	@Test
	void givenADouble_whenNotCloseTo_thenCorrect() {
		double actual = 1.6;
		double operand = 1;
		double error = 0.5;
		assertThat(actual, is(not(closeTo(operand, error))));
	}

	@Test
	void givenABigDecimal_whenCloseTo_thenCorrect() {
		BigDecimal actual = new BigDecimal("1.0003");
		BigDecimal operand = new BigDecimal("1");
		BigDecimal error = new BigDecimal("0.0005");
		assertThat(actual, is(closeTo(operand, error)));
	}

	@Test
	void givenABigDecimal_whenNotCloseTo_thenCorrect() {
		BigDecimal actual = new BigDecimal("1.0006");
		BigDecimal operand = new BigDecimal("1");
		BigDecimal error = new BigDecimal("0.0005");
		assertThat(actual, is(not(closeTo(operand, error))));
	}

	@Test
	void given5_whenComparesEqualTo5_thenCorrect() {
		Integer five = 5;
		assertThat(five, comparesEqualTo(five));
	}

	@Test
	void given5_whenNotComparesEqualTo7_thenCorrect() {
		Integer seven = 7;
		Integer five = 5;
		assertThat(five, not(comparesEqualTo(seven)));
	}

	@Test
	void given7_whenGreaterThan5_thenCorrect() {
		Integer seven = 7;
		Integer five = 5;
		assertThat(seven, is(greaterThan(five)));
	}

	@Test
	void given7_whenGreaterThanOrEqualTo5_thenCorrect() {
		Integer seven = 7;
		Integer five = 5;
		assertThat(seven, is(greaterThanOrEqualTo(five)));
	}

	@Test
	void given5_whenGreaterThanOrEqualTo5_thenCorrect() {
		Integer five = 5;
		assertThat(five, is(greaterThanOrEqualTo(five)));
	}

	@Test
	void given3_whenLessThan5_thenCorrect() {
		Integer three = 3;
		Integer five = 5;
		assertThat(three, is(lessThan(five)));
	}

	@Test
	void given3_whenLessThanOrEqualTo5_thenCorrect() {
		Integer three = 3;
		Integer five = 5;
		assertThat(three, is(lessThanOrEqualTo(five)));
	}

	@Test
	void given5_whenLessThanOrEqualTo5_thenCorrect() {
		Integer five = 5;
		assertThat(five, is(lessThanOrEqualTo(five)));
	}

	@Test
	void givenBenjamin_whenGreaterThanAmanda_thenCorrect() {
		String amanda = "Amanda";
		String benjamin = "Benjamin";
		assertThat(benjamin, is(greaterThan(amanda)));
	}

	@Test
	void givenAmanda_whenLessThanBenajmin_thenCorrect() {
		String amanda = "Amanda";
		String benjamin = "Benjamin";
		assertThat(amanda, is(lessThan(benjamin)));
	}

	@Test
	void givenToday_whenGreaterThanYesterday_thenCorrect() {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		assertThat(today, is(greaterThan(yesterday)));
	}

	@Test
	void givenToday_whenLessThanTomorrow_thenCorrect() {
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		assertThat(today, is(lessThan(tomorrow)));
	}

	@Test
	void givenAmanda_whenOlderThanBenjamin_thenCorrect() {
		Person amanda = new Person("Amanda", 20);
		Person benjamin = new Person("Benjamin", 18);
		assertThat(amanda, is(greaterThan(benjamin)));
	}

	@Test
	void givenBenjamin_whenYoungerThanAmanda_thenCorrect() {
		Person amanda = new Person("Amanda", 20);
		Person benjamin = new Person("Benjamin", 18);
		assertThat(benjamin, is(lessThan(amanda)));
	}

	static class Person implements Comparable<Person> {
		String name;
		int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		String getName() {
			return name;
		}

		int getAge() {
			return age;
		}

		@Override
		public int compareTo(Person o) {
			if (this.age == o.getAge()) return 0;
			if (this.age > o.age) return 1;
			else return -1;
		}
	}

	@Test
	void givenNaN_whenIsNotANumber_thenCorrect() {
		double zero = 0d;
		assertThat(zero / zero, is(notANumber()));
	}
}