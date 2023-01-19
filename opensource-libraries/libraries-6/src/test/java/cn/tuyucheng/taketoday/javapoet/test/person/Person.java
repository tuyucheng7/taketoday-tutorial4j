package cn.tuyucheng.taketoday.javapoet.test.person;

public interface Person {
	String DEFAULT_NAME = "Alice";

	String getName();

	default String getDefaultName() {
		return DEFAULT_NAME;
	}
}
