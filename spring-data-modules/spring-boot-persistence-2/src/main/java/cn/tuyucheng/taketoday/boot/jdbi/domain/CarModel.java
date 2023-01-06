package cn.tuyucheng.taketoday.boot.jdbi.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarModel {

	private Long id;
	private String name;
	private Integer year;
	private String sku;
	private Long makerId;
}