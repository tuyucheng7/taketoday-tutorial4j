package cn.tuyucheng.taketoday.transferqueue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
class TransferQueueIntegrationTest {

	@Test
	void whenMultipleConsumersAndProducers_thenProcessAllMessages() throws InterruptedException {
		// given
		TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
		ExecutorService executor = Executors.newFixedThreadPool(3);
		Producer producer1 = new Producer(transferQueue, "1", 3);
		Producer producer2 = new Producer(transferQueue, "2", 3);
		Consumer consumer1 = new Consumer(transferQueue, "1", 3);
		Consumer consumer2 = new Consumer(transferQueue, "2", 3);

		// when
		executor.execute(producer1);
		executor.execute(producer2);
		executor.execute(consumer1);
		executor.execute(consumer2);

		// then
		executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
		executor.shutdown();

		assertEquals(3, producer1.numberOfProducedMessages.intValue());
		assertEquals(3, producer2.numberOfProducedMessages.intValue());
	}

	@Test
	void whenUseOneConsumerAndOneProducer_thenShouldProcessAllMessages() throws InterruptedException {
		// given
		TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Producer producer = new Producer(transferQueue, "1", 3);
		Consumer consumer = new Consumer(transferQueue, "1", 3);

		// when
		executor.execute(producer);
		executor.execute(consumer);

		// then
		executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
		executor.shutdown();

		assertEquals(3, producer.numberOfProducedMessages.intValue());
		assertEquals(3, consumer.numberOfConsumedMessages.intValue());
	}

	@Test
	void whenUseOneProducerAndNoConsumers_thenShouldFailWithTimeout() throws InterruptedException {
		// given
		TransferQueue<String> transferQueue = new LinkedTransferQueue<>();
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Producer producer = new Producer(transferQueue, "1", 3);

		// when
		executor.execute(producer);

		// then
		executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
		executor.shutdown();

		assertEquals(0, producer.numberOfProducedMessages.intValue());
	}
}