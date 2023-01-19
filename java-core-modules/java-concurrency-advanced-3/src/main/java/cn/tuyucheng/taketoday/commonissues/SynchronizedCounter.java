package cn.tuyucheng.taketoday.commonissues;

class SynchronizedCounter {
	private int counter = 0;

	public synchronized void increment() {
		counter++;
	}

	public synchronized int getValue() {
		return counter;
	}
}