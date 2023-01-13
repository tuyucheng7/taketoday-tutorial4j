package cn.tuyucheng.taketoday.mdc.log4j2;

import cn.tuyucheng.taketoday.mdc.TransactionFactory;
import cn.tuyucheng.taketoday.mdc.Transfer;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DemoIntegrationTest {

	@Test
	public void main() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(3);
		TransactionFactory transactionFactory = new TransactionFactory();
		for (int i = 0; i < 10; i++) {
			Transfer tx = transactionFactory.newInstance();
			Runnable task = new Log4J2Runnable(tx);
			executor.submit(task);
		}
		executor.shutdown();
		executor.awaitTermination(60, TimeUnit.SECONDS);
	}
}
