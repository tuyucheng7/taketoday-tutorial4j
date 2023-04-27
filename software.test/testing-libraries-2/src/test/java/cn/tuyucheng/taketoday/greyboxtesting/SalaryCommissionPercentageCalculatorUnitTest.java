package cn.tuyucheng.taketoday.greyboxtesting;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Level.L1;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Level.L2;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Level.L3;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.SalesImpact;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.SalesImpact.HIGH;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.SalesImpact.LOW;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.SalesImpact.MEDIUM;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Seniority;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Seniority.JR;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Seniority.MID;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Seniority.SR;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Type;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Type.CONTRACTOR;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Type.FREELANCER;
import static cn.tuyucheng.taketoday.greyboxtesting.SalaryCommissionPercentageCalculator.Type.FULL_TIME_COMMISSIONED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SalaryCommissionPercentageCalculatorUnitTest {

	private final SalaryCommissionPercentageCalculator testTarget = new SalaryCommissionPercentageCalculator();

	@ParameterizedTest
	@MethodSource("provideReferenceTestScenarioTable")
	void givenReferenceTable_whenCalculateAverageCommission_thenReturnExpectedResult(SalaryCommissionPercentageCalculator.Level level,
																					 Type type,
																					 Seniority seniority,
																					 SalesImpact impact,
																					 double expected) {
		BigDecimal got = testTarget.calculate(level, type, seniority, impact);

		assertEquals(BigDecimal.valueOf(expected), got);
	}

	private static Stream<Arguments> provideReferenceTestScenarioTable() {
		return Stream.of(
			Arguments.of(L1, FULL_TIME_COMMISSIONED, JR, LOW, 0.26),
			Arguments.of(L1, CONTRACTOR, SR, MEDIUM, 0.12),
			Arguments.of(L1, FREELANCER, MID, HIGH, 0.11),
			Arguments.of(L2, FULL_TIME_COMMISSIONED, SR, HIGH, 0.18),
			Arguments.of(L2, CONTRACTOR, MID, LOW, 0.11),
			Arguments.of(L2, FREELANCER, JR, MEDIUM, 0.24),
			Arguments.of(L3, FULL_TIME_COMMISSIONED, MID, MEDIUM, 0.17),
			Arguments.of(L3, CONTRACTOR, JR, HIGH, 0.28),
			Arguments.of(L3, FREELANCER, SR, LOW, 0.12)
		);
	}
}