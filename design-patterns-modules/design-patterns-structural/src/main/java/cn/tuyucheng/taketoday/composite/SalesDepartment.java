package cn.tuyucheng.taketoday.composite;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tuyucheng
 */
public class SalesDepartment implements Department {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesDepartment.class);

	private Integer id;
	private String name;

	public SalesDepartment(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public void printDepartmentName() {
		LOGGER.info(getClass().getSimpleName());
	}

	@ExcludeFromJacocoGeneratedReport
	public Integer getId() {
		return id;
	}

	@ExcludeFromJacocoGeneratedReport
	public void setId(Integer id) {
		this.id = id;
	}

	@ExcludeFromJacocoGeneratedReport
	public String getName() {
		return name;
	}

	@ExcludeFromJacocoGeneratedReport
	public void setName(String name) {
		this.name = name;
	}
}