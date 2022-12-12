package cn.tuyucheng.taketoday.java8.lambda.serialization;

public class NotSerializableLambdaExpression {
	public static Object getLambdaExpressionObject() {
		return (Runnable) () -> System.out.println("please serialize this message");
	}
}