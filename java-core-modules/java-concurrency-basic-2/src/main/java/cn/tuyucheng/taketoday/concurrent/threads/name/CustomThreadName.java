package cn.tuyucheng.taketoday.concurrent.threads.name;

public class CustomThreadName {
	public int currentNumber = 1;

	public int N = 5;

	public static void main(String[] args) {
		CustomThreadName test = new CustomThreadName();

		// Uncomment below to set thread name using setName() Method
		// Thread.currentThread().setName("ODD");
		Thread oddThread = new Thread(test::printOddNumber, "ODD");
		// or Uncomment below to set thread name using setName() Method
		// oddThread.setName("ODD");

		// Uncomment below to set thread name using setName() Method
		// Thread.currentThread().setName("EVEN");
		Thread evenThread = new Thread(test::printEvenNumber, "EVEN");

		// evenThread.setName("EVEN");

		evenThread.start();
		oddThread.start();
	}

	public void printEvenNumber() {
		synchronized (this) {
			while (currentNumber < N) {
				while (currentNumber % 2 == 1) {
					try {
						wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				System.out.println(Thread.currentThread().getName() + " --> " + currentNumber);
				currentNumber++;
				notify();
			}
		}
	}

	public void printOddNumber() {
		synchronized (this) {
			while (currentNumber < N) {
				while (currentNumber % 2 == 0) {
					try {
						wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				System.out.println(Thread.currentThread().getName() + " --> " + currentNumber);
				currentNumber++;
				notify();
			}
		}
	}
}