package cn.tuyucheng.taketoday.java8.lambda.serialization;

import java.io.Serializable;

public class SerializableLambdaExpression {
	public static Object getLambdaExpressionObject() {
		return (Runnable & Serializable) () -> System.out.println("please serialize this message");
	}
}