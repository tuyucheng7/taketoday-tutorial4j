package cn.tuyucheng.taketoday.graphql.error.handling.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
	@Id
	private String vin;
	private Integer year;
	private String make;
	private String model;
	private String trim;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_location")
	private Location location;
}