package cn.tuyucheng.taketoday.factorymethod;

public class InstanceBarFactory {

	public Bar createInstance(String name) {
		return new Bar(name);
	}
}
