package cn.tuyucheng.taketoday.object.type;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

public class Evergreen implements Tree {

	private String name;

	public Evergreen(String name) {
		this.name = name;
	}


	@Override
	@ExcludeFromJacocoGeneratedReport
	public boolean isEvergreen() {
		return true;
	}
}