package cn.tuyucheng.taketoday.collection;

public class BaeldungBean {

	private String name;

	public BaeldungBean(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
