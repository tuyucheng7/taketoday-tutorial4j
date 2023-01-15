package cn.tuyucheng.taketoday.strategy;

import java.math.BigDecimal;

public class NationalDiscounter implements Discounter {

	@Override
	public BigDecimal apply(BigDecimal amount) {
		return amount.multiply(BigDecimal.valueOf(0.5));
	}
}