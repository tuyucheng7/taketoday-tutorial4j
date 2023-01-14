package cn.tuyucheng.taketoday.instanceof_alternatives.visitorspattern;

public interface Visitor {

	String visit(Anatotitan anatotitan);

	String visit(Euraptor euraptor);
}