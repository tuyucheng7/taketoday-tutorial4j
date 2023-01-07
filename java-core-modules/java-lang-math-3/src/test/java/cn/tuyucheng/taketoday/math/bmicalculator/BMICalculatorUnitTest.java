package cn.tuyucheng.taketoday.math.bmicalculator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BMICalculatorUnitTest {

	@Test
	public void whenBMIIsGreaterThanThirty_thenObese() {

		double weight = 100;
		double height = 1.524;
		String actual = BMICalculator.calculateBMI(weight, height);
		String expected = "Obese";

		assertThat(actual).isEqualTo(expected);

	}
}
