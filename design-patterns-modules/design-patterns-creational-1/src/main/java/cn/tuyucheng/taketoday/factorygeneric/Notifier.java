package cn.tuyucheng.taketoday.factorygeneric;

public interface Notifier<T> {

	void notify(T obj);
}