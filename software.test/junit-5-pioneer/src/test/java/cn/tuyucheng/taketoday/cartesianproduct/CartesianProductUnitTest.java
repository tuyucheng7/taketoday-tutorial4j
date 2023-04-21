package cn.tuyucheng.taketoday.cartesianproduct;

import cn.tuyucheng.taketoday.AnotherEnum;
import cn.tuyucheng.taketoday.MyEnum;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junitpioneer.jupiter.cartesian.ArgumentSets;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Enum;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Values;
import org.junitpioneer.jupiter.params.LongRangeSource;
import org.junitpioneer.jupiter.params.ShortRangeSource;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junitpioneer.jupiter.cartesian.CartesianTest.Enum.Mode.EXCLUDE;
import static org.junitpioneer.jupiter.cartesian.CartesianTest.Enum.Mode.MATCH_ALL;

public class CartesianProductUnitTest {

	@CartesianTest
	void simple_cartesian_product_test(@Values(ints = {1, 2}) int x,
									   @Values(ints = {3, 4}) int y) {
		assertThat(x).isGreaterThan(0);
		assertThat(y).isGreaterThan(0);
	}

	@CartesianTest
	void givenEnumParam_thenCorrect(@Enum ChronoUnit unit) {
		assertThat(unit).isNotNull();
	}

	@CartesianTest
	void givenNonEnumParam_whenExplicitValueProperty_thenCorrect(@Enum(ChronoUnit.class) TemporalUnit unit) {
		assertThat(unit).isNotNull();
	}

	@CartesianTest
	void givenEnumParam_whenExplicitNamesProperty_thenCorrect(@Enum(names = {"DAYS", "HOURS"}) ChronoUnit unit) {
		assertThat(EnumSet.of(ChronoUnit.DAYS, ChronoUnit.HOURS)).contains(unit);
	}

	@CartesianTest
	void givenEnumParam_whenExcludeWithModeProperty_thenCorrect(@Enum(mode = EXCLUDE, names = {"ERAS", "FOREVER"})
																ChronoUnit unit) {
		assertThat(EnumSet.of(ChronoUnit.ERAS, ChronoUnit.FOREVER)).doesNotContain(unit);
	}

	@CartesianTest
	void givenEnumParam_whenMatchesWithModeProperty_thenCorrect(@Enum(mode = MATCH_ALL, names = "^.*DAYS$") ChronoUnit unit) {
		assertThat(unit.name()).endsWith("DAYS");
	}

	@CartesianTest
	void givenTwoEnumParams_thenCorrect(@Enum MyEnum myEnum,
										@Enum(names = {"ALPHA", "DELTA"}, mode = Enum.Mode.EXCLUDE) AnotherEnum anotherEnum) {
		assertThat(EnumSet.of(MyEnum.ONE, MyEnum.TWO, MyEnum.THREE)).contains(myEnum);
		assertThat(EnumSet.of(AnotherEnum.ALPHA, AnotherEnum.DELTA)).doesNotContain(anotherEnum);
	}

	@CartesianTest
	void givenTwoRangeSource_thenCorrect(@ShortRangeSource(from = 1, to = 10, step = 2) short odd,
										 @LongRangeSource(from = 0L, to = 10L, step = 2, closed = true) long even) {
		assertThat(odd % 2 != 0).isTrue();
		assertThat(even % 2 == 0).isTrue();
	}

	@CartesianTest
	@CartesianTest.MethodFactory("stringClassTimeUnitFactory")
	void givenArgumentsFactoryMethod_thenCorrect(String symbol, Class<?> clazz, TimeUnit unit) {
		assertThat(symbol).isIn("Alpha", "Omega");
		assertThat(EnumSet.of(TimeUnit.DAYS, TimeUnit.HOURS)).contains(unit);
	}

	@Disabled("fails because we try to supply a non-existent integer parameter")
	@CartesianTest
	@CartesianTest.MethodFactory("unsuitableStringIntFactory")
	void testHasTooFewParameters(String string) {
	}

	@Disabled("fails because the boolean parameter is not resolved")
	@CartesianTest
	@CartesianTest.MethodFactory("unsuitableStringIntFactory")
	void testHasTooManyParameters(String string, int i, boolean b) {
	}

	@Disabled("fails because the factory method declared parameter sets in the wrong order")
	@CartesianTest
	@CartesianTest.MethodFactory("unsuitableStringIntFactory")
	void testHasParametersInWrongOrder(int i, String string) {
	}

	static ArgumentSets stringClassTimeUnitFactory() {
		return ArgumentSets
			.argumentsForFirstParameter("Alpha", "Omega")
			.argumentsForNextParameter(String.class, StringBuffer.class, StringBuffer.class)
			.argumentsForNextParameter(TimeUnit.DAYS, TimeUnit.HOURS);
	}

	static ArgumentSets unsuitableStringIntFactory() {
		return ArgumentSets
			.argumentsForFirstParameter("A", "B", "C")
			.argumentsForNextParameter(1, 2, 3);
	}

	@CartesianTest
	void givenCustomIntArgumentProvider_thenCorrect(@Ints({1, 2}) int x, @Ints({3, 4}) int y) {
		assertThat(x).isGreaterThan(0);
		assertThat(y).isGreaterThan(0);
	}

	@CartesianTest
	void givenCustomPersonArgumentProvider_thenCorrect(@People(names = {"Alice", "Bob"}, ages = {20, 22}) Person person) {
		assertThat(person.getName()).isIn("Alice", "Bob");
		assertThat(person.getAge()).isIn(20, 22);
	}

	@CartesianTest
	void givenCustomPersonArgumentAnnotationProvider_thenCorrect(@People(names = {"Alice", "Bob"}, ages = {20, 22}) Person person) {
		assertThat(person.getName()).isIn("Alice", "Bob");
		assertThat(person.getAge()).isIn(20, 22);
	}

	@DisplayName("Basic bit test")
	@CartesianTest(name = "{index} => first bit: {0} second bit: {1}")
	void testWithCustomDisplayName(@Values(strings = {"0", "1"}) String a,
								   @Values(strings = {"0", "1"}) String b) {
		assertThat(a).isIn("0", "1");
		assertThat(b).isIn("0", "1");
	}
}