package cn.tuyucheng.taketoday.domain;

import cn.tuyucheng.taketoday.constants.OrderStatus;
import cn.tuyucheng.taketoday.serdeser.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document
public class Order {

	@Id
	@JsonSerialize(using = ObjectIdSerializer.class)
	private ObjectId id;
	private String userId;
	private Long total;
	private String paymentMode;
	private Address shippingAddress;
	private LocalDate shippingDate;
	private OrderStatus orderStatus;
	private String responseMessage;

	public Order setShippingDate(LocalDate shippingDate) {
		this.shippingDate = shippingDate;
		return this;
	}

	public Order setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
		return this;
	}

	public Order setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
		return this;
	}

}
