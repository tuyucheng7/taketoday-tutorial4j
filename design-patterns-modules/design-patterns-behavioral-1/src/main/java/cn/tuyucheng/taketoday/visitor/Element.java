package cn.tuyucheng.taketoday.visitor;

public abstract class Element {

	public String uuid;

	protected Element(String uuid) {
		this.uuid = uuid;
	}

	public abstract void accept(Visitor v);
}