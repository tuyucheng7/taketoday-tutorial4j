package cn.tuyucheng.taketoday.getbean;

class Tiger implements AnnotationConfig.Animal {
	private String name;

	Tiger(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}
}
