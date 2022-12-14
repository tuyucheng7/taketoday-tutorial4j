package cn.tuyucheng.taketoday.reducingifelse;

public interface Rule {

	boolean evaluate(Expression expression);

	Result getResult();
}