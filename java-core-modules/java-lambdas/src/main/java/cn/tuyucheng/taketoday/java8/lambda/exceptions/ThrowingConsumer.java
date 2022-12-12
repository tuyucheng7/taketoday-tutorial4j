package cn.tuyucheng.taketoday.java8.lambda.exceptions;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

    void accept(T t) throws E;
}