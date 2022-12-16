package cn.tuyucheng.taketoday.dip.entities;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

public class Customer {

	private final String name;

	public Customer(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	@ExcludeFromJacocoGeneratedReport
	public String toString() {
		return "Customer{" + "name=" + name + '}';
	}
}