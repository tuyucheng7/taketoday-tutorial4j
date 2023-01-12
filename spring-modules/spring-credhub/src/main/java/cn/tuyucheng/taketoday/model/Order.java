package cn.tuyucheng.taketoday.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Order {

	private Long id;
	private String customerName;
	private LocalDate orderDate;
}
