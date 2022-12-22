package cn.tuyucheng.taketoday.object.type;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

public class Deciduous implements Tree {

	private String name;

	public Deciduous(String name) {
		this.name = name;
	}

	@Override
	@ExcludeFromJacocoGeneratedReport
	public boolean isEvergreen() {
		return false;
	}
}