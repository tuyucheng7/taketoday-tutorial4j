package cn.tuyucheng.taketoday.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tuyucheng
 */
public class FinancialDepartment implements Department {
	private static final Logger LOGGER = LoggerFactory.getLogger(FinancialDepartment.class);

	private Integer id;
	private String name;

	public FinancialDepartment(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public void printDepartmentName() {
		LOGGER.info(getClass().getSimpleName());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}