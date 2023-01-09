package cn.tuyucheng.taketoday.mockito.fluentapi;

public class PizzaService {

	private Pizza.PizzaBuilder builder;

	public PizzaService(Pizza.PizzaBuilder builder) {
		this.builder = builder;
	}

	public Pizza orderHouseSpecial() {
		return builder.name("Special")
			.size(Pizza.PizzaSize.LARGE)
			.withExtraTopping("Mushrooms")
			.withStuffedCrust(true)
			.withExtraTopping("Chilli")
			.willCollect(true)
			.applyDiscount(20)
			.build();
	}
}
