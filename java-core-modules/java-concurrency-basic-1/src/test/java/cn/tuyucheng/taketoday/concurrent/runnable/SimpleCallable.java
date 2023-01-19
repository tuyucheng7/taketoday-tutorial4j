package cn.tuyucheng.taketoday.concurrent.runnable;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.Callable;

class SimpleCallable implements Callable<Integer> {

	@Override
	public Integer call() {
		return RandomUtils.nextInt(0, 100);
	}
}