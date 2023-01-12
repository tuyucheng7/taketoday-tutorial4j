package cn.tuyucheng.taketoday.javaxval.bigdecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

public class Invoice {

	@DecimalMin(value = "0.0", inclusive = false)
	@Digits(integer = 3, fraction = 2)
	private BigDecimal price;
	private String description;

	public Invoice(BigDecimal price, String description) {
		this.price = price;
		this.description = description;
	}
}
