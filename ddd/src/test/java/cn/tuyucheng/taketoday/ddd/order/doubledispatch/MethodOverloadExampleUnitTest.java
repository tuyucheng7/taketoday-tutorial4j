package cn.tuyucheng.taketoday.ddd.order.doubledispatch;

import cn.tuyucheng.taketoday.ddd.order.OrderFixtureUtils;
import cn.tuyucheng.taketoday.ddd.order.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodOverloadExampleUnitTest {
	// @formatter:off
	@DisplayName(
		"given discount policy accepting special orders, " +
			"when apply the policy on special order declared as regular order, " +
			"then regular discount method is used"
	)
// @formatter:on
	@Test
	void test() throws Exception {
		// given
		SpecialDiscountPolicy specialPolicy = new SpecialDiscountPolicy() {
			@Override
			public double discount(Order order) {
				return 0.01;
			}

			@Override
			public double discount(SpecialOrder order) {
				return 0.10;
			}
		};
		Order specialOrder = new SpecialOrder(anyOrderLines());

		// when
		double discount = specialPolicy.discount(specialOrder);

		// then
		assertThat(discount).isEqualTo(0.01);
	}

	private List<OrderLine> anyOrderLines() {
		return OrderFixtureUtils.anyOrderLines();
	}
}
