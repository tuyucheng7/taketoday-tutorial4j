package cn.tuyucheng.taketoday.abstractconstructors.noargs;

public class SimpleCounter extends Counter {

	public SimpleCounter(int value) {
		super(value);
	}

	@Override
	int increment() {
		return ++value;
	}
}
