package cn.tuyucheng.taketoday.concurrent.volatilekeyword;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedObject {
	private final AtomicInteger count = new AtomicInteger(0);

	void incrementCount() {
		count.incrementAndGet();
	}

	public int getCount() {
		return count.get();
	}
}