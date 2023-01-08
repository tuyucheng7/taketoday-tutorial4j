package cn.tuyucheng.taketoday.jackson.csv;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonPropertyOrder({
	"count",
	"name"
})
public abstract class OrderLineForCsv {

	@JsonProperty("name")
	private String item;

	@JsonProperty("count")
	private int quantity;

	@JsonIgnore
	private BigDecimal unitPrice;


}
