package cn.tuyucheng.taketoday.concurrent.cyclicbarrier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.lang.Thread.currentThread;

public class CyclicBarrierDemo {
	private CyclicBarrier cyclicBarrier;
	private final List<List<Integer>> partialResults = Collections.synchronizedList(new ArrayList<>());
	private final Random random = new Random();
	private int NUM_PARTIAL_RESULTS;
	private int NUM_WORKERS;

	private void runSimulation() {
		NUM_PARTIAL_RESULTS = 3;
		NUM_WORKERS = 5;

		cyclicBarrier = new CyclicBarrier(NUM_WORKERS, new AggregatorThread());
		System.out.println("Spawning " + NUM_WORKERS + " worker threads to compute " + NUM_PARTIAL_RESULTS + " partial results each");
		for (int i = 0; i < NUM_WORKERS; i++) {
			Thread worker = new Thread(new NumberCruncherThread());
			worker.setName("Thread " + i);
			worker.start();
		}
	}

	class NumberCruncherThread implements Runnable {

		@Override
		public void run() {
			String thisThreadName = currentThread().getName();
			List<Integer> partialResult = new ArrayList<>();
			for (int i = 0; i < NUM_PARTIAL_RESULTS; i++) {
				int num = random.nextInt(10);
				System.out.println(thisThreadName + ": Crunching some numbers! Final result - " + num);
				partialResult.add(num);
			}
			partialResults.add(partialResult);
			try {
				System.out.println(thisThreadName + " waiting for others to reach barrier.");
				cyclicBarrier.await();
			} catch (BrokenBarrierException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	class AggregatorThread implements Runnable {

		@Override
		public void run() {
			String thisThreadName = currentThread().getName();
			System.out.println(thisThreadName + ": Computing final sum of " + NUM_WORKERS + " worker, having " + NUM_PARTIAL_RESULTS + " results each.");
			int sum = 0;
			for (List<Integer> threadResult : partialResults) {
				for (Integer partialResult : threadResult) {
					System.out.print(partialResult + " ");
					sum += partialResult;
				}
				System.out.println();
			}
			System.out.println(currentThread().getName() + ": Final result = " + sum);
		}
	}

	public static void main(String[] args) {
		CyclicBarrierDemo play = new CyclicBarrierDemo();
		play.runSimulation();
	}
}