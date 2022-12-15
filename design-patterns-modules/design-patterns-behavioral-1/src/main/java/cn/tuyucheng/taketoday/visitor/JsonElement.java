package cn.tuyucheng.taketoday.visitor;

public class JsonElement extends Element {

	public JsonElement(String uuid) {
		super(uuid);
	}

	public void accept(Visitor v) {
		v.visit(this);
	}
}