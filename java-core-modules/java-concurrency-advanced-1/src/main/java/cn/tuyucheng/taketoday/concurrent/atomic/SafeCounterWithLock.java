package cn.tuyucheng.taketoday.concurrent.atomic;

public class SafeCounterWithLock {
	private int counter;

	int getValue() {
		return counter;
	}

	synchronized void increment() {
		counter++;
	}
}