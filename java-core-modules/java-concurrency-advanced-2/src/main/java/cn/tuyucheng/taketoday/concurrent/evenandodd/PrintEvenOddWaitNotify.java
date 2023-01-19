package cn.tuyucheng.taketoday.concurrent.evenandodd;

import static java.lang.Thread.currentThread;

public class PrintEvenOddWaitNotify {

	public static void main(String... args) {
		Printer printer = new Printer();
		Thread t1 = new Thread(new TaskEvenOdd(10, printer, false), "Odd");
		Thread t2 = new Thread(new TaskEvenOdd(10, printer, true), "Even");
		t1.start();
		t2.start();
	}

	static class Printer {
		private volatile boolean isOdd;

		synchronized void printEven(int number) {
			while (!isOdd) {
				try {
					wait();
				} catch (InterruptedException e) {
					currentThread().interrupt();
				}
			}
			System.out.println(currentThread().getName() + ":" + number);
			isOdd = false;
			notify();
		}

		synchronized void printOdd(int number) {
			while (isOdd) {
				try {
					wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			System.out.println(currentThread().getName() + ":" + number);
			isOdd = true;
			notify();
		}
	}

	static class TaskEvenOdd implements Runnable {
		private final int max;
		private final Printer printer;
		private final boolean isEvenNumber;

		public TaskEvenOdd(int max, Printer printer, boolean isEvenNumber) {
			this.max = max;
			this.printer = printer;
			this.isEvenNumber = isEvenNumber;
		}

		@Override
		public void run() {
			int number = isEvenNumber ? 2 : 1;
			while (number <= max) {
				if (isEvenNumber) {
					printer.printEven(number);
				} else {
					printer.printOdd(number);
				}
				number += 2;
			}
		}
	}
}