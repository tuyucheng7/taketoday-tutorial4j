package cn.tuyucheng.taketoday.creational.abstractfactory;

public interface AbstractFactory<T> {
	T create(String type);
}