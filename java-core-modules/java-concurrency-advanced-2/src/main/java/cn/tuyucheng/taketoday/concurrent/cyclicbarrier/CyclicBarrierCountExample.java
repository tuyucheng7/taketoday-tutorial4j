package cn.tuyucheng.taketoday.concurrent.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierCountExample {
	private final int count;

	public CyclicBarrierCountExample(int count) {
		this.count = count;
	}

	public boolean callTwiceInSameThread() {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
		Thread t = new Thread(() -> {
			try {
				cyclicBarrier.await();
				cyclicBarrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				Thread.currentThread().interrupt();
			}
		});
		t.start();
		return cyclicBarrier.isBroken();
	}

	public static void main(String[] args) {
		CyclicBarrierCountExample ex = new CyclicBarrierCountExample(2);
		System.out.println("Count : " + ex.callTwiceInSameThread());
	}
}