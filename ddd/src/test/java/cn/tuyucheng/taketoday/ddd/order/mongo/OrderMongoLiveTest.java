package cn.tuyucheng.taketoday.ddd.order.mongo;

import cn.tuyucheng.taketoday.ddd.order.Order;
import cn.tuyucheng.taketoday.ddd.order.OrderLine;
import cn.tuyucheng.taketoday.ddd.order.Product;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 To run this test we need to run the databases first.
 A dedicated docker-compose.yml file is located under the resources directory.
 We can run it by simple executing `docker-compose up`.
 */
@SpringJUnitConfig
@SpringBootTest
public class OrderMongoLiveTest {
	@Autowired
	private OrderMongoRepository repo;

	@DisplayName("given order with two line items, when persist using mongo repository, then order is saved")
	@Test
	void test() throws Exception {
		// given
		Order order = prepareTestOrderWithTwoLineItems();

		// when
		repo.save(order);

		// then
		List<Order> foundOrders = repo.findAll();
		assertThat(foundOrders).hasSize(1);
		List<OrderLine> foundOrderLines = foundOrders.iterator()
			.next()
			.getOrderLines();
		assertThat(foundOrderLines).hasSize(2);
		assertThat(foundOrderLines).containsOnlyElementsOf(order.getOrderLines());
	}

	private Order prepareTestOrderWithTwoLineItems() {
		OrderLine ol0 = new OrderLine(new Product(Money.of(CurrencyUnit.USD, 10.00)), 2);
		OrderLine ol1 = new OrderLine(new Product(Money.of(CurrencyUnit.USD, 5.00)), 10);
		return new Order(Arrays.asList(ol0, ol1));
	}
}
