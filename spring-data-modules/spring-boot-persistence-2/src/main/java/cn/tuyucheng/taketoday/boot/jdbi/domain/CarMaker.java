package cn.tuyucheng.taketoday.boot.jdbi.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CarMaker {

	private Long id;
	private String name;
	private List<CarModel> models;
}