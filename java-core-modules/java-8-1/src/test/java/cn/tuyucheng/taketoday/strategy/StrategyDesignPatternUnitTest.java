package cn.tuyucheng.taketoday.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static cn.tuyucheng.taketoday.strategy.Discounter.national;
import static cn.tuyucheng.taketoday.strategy.Discounter.newYear;
import static cn.tuyucheng.taketoday.strategy.Discounter.springFestival;
import static org.assertj.core.api.Assertions.assertThat;

class StrategyDesignPatternUnitTest {

	@Test
	void shouldDivideByTwo_WhenApplyingStaffDiscounter() {
		Discounter staffDiscounter = new NationalDiscounter();

		final BigDecimal discountedValue = staffDiscounter
			.apply(BigDecimal.valueOf(100));

		assertThat(discountedValue)
			.isEqualByComparingTo(BigDecimal.valueOf(50));
	}

	@Test
	void shouldDivideByTwo_WhenApplyingStaffDiscounterWithAnonymousTypes() {
		Discounter staffDiscounter = new Discounter() {
			@Override
			public BigDecimal apply(BigDecimal amount) {
				return amount.multiply(BigDecimal.valueOf(0.5));
			}
		};

		final BigDecimal discountedValue = staffDiscounter
			.apply(BigDecimal.valueOf(100));

		assertThat(discountedValue)
			.isEqualByComparingTo(BigDecimal.valueOf(50));
	}

	@Test
	void shouldDivideByTwo_WhenApplyingStaffDiscounterWithLambda() {
		Discounter staffDiscounter = amount -> amount.multiply(BigDecimal.valueOf(0.5));

		final BigDecimal discountedValue = staffDiscounter
			.apply(BigDecimal.valueOf(100));

		assertThat(discountedValue)
			.isEqualByComparingTo(BigDecimal.valueOf(50));
	}

	@Test
	void shouldApplyAllDiscounts() {
		List<Discounter> discounters = Arrays.asList(springFestival(), newYear(), national());

		BigDecimal amount = BigDecimal.valueOf(100);

		final Discounter combinedDiscounter = discounters
			.stream()
			.reduce(v -> v, Discounter::combine);

		combinedDiscounter.apply(amount);
	}

	@Test
	void shouldChainDiscounters() {
		final Function<BigDecimal, BigDecimal> combinedDiscounters = Discounter
			.springFestival()
			.andThen(newYear());

		combinedDiscounters.apply(BigDecimal.valueOf(100));
	}
}