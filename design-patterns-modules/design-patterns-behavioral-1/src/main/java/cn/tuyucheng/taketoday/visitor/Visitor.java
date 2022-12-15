package cn.tuyucheng.taketoday.visitor;

public interface Visitor {

	void visit(XmlElement xe);

	void visit(JsonElement je);
}