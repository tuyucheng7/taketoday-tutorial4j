package com.baeldung.jpa.multipletables.multipleentities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "meal")
public class MealWithMultipleEntities {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "price")
	private BigDecimal price;

	@OneToOne(mappedBy = "meal")
	private AllergensAsEntity allergens;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public AllergensAsEntity getAllergens() {
		return allergens;
	}

	public void setAllergens(AllergensAsEntity allergens) {
		this.allergens = allergens;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "MealWithMultipleEntities [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price + ", allergens=" + allergens + "]";
	}

}
