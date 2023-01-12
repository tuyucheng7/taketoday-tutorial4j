package cn.tuyucheng.taketoday.ddd.order.doubledispatch;

import cn.tuyucheng.taketoday.ddd.order.OrderFixtureUtils;
import cn.tuyucheng.taketoday.ddd.order.OrderLine;
import cn.tuyucheng.taketoday.ddd.order.doubledispatch.visitor.HtmlOrderViewCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlOrderViewCreatorUnitTest {
	// @formatter:off
	@DisplayName(
		"given collection of regular and special orders, " +
			"when create HTML view using visitor for each order, " +
			"then the dedicated view is created for each order"
	)
	// @formatter:on
	@Test
	void test() throws Exception {
		// given
		List<OrderLine> anyOrderLines = OrderFixtureUtils.anyOrderLines();
		List<Order> orders = Arrays.asList(new Order(anyOrderLines), new SpecialOrder(anyOrderLines));
		HtmlOrderViewCreator htmlOrderViewCreator = new HtmlOrderViewCreator();

		// when
		orders.get(0)
			.accept(htmlOrderViewCreator);
		String regularOrderHtml = htmlOrderViewCreator.getHtml();
		orders.get(1)
			.accept(htmlOrderViewCreator);
		String specialOrderHtml = htmlOrderViewCreator.getHtml();

		// then
		assertThat(regularOrderHtml).containsPattern("<p>Regular order total cost: .*</p>");
		assertThat(specialOrderHtml).containsPattern("<h1>Special Order</h1><p>total cost: .*</p>");
	}
}
