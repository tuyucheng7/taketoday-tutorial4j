package cn.tuyucheng.taketoday.mockito.fluentapi;

import cn.tuyucheng.taketoday.mockito.fluentapi.Pizza.PizzaSize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PizzaUnitTest {

	@Test
	public void givenFluentPizzaApi_whenBuilt_thenPizzaHasCorrectAttributes() {
		Pizza pizza = new Pizza.PizzaBuilder()
			.name("Margherita")
			.size(PizzaSize.LARGE)
			.withExtraTopping("Mushroom")
			.withStuffedCrust(false)
			.willCollect(true)
			.applyDiscount(20)
			.build();

		assertEquals("Pizza name: ", "Margherita", pizza.getName());
		assertEquals("Pizza size: ", PizzaSize.LARGE, pizza.getSize());
		assertEquals("Extra toppings: ", "Mushroom", pizza.getToppings()
			.get(0));
		assertFalse("Has stuffed crust: ", pizza.isStuffedCrust());
		assertTrue("Will collect: ", pizza.isCollecting());
		assertEquals("Discounts: ", Integer.valueOf(20), pizza.getDiscount());
	}
}
