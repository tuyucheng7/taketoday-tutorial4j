package cn.tuyucheng.taketoday.samples.endpoints;

public interface Activator<T> {

	public void handleMessage(T input);

}
