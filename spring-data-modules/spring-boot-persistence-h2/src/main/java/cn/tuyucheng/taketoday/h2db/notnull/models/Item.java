package cn.tuyucheng.taketoday.h2db.notnull.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class Item {

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	// @Column(nullable = false)
	private BigDecimal price;
}