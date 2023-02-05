package cn.tuyucheng.taketoday.strategy;

import java.math.BigDecimal;

public class SpringFestivalDiscounter implements Discounter {

	@Override
	public BigDecimal apply(BigDecimal amount) {
		return amount.multiply(BigDecimal.valueOf(0.9));
	}
}