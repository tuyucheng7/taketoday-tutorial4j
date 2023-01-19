package cn.tuyucheng.taketoday.transferqueue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.*;

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

		assertEquals(producer1.numberOfProducedMessages.intValue(), 3);
		assertEquals(producer2.numberOfProducedMessages.intValue(), 3);
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

		assertEquals(producer.numberOfProducedMessages.intValue(), 3);
		assertEquals(consumer.numberOfConsumedMessages.intValue(), 3);
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

		assertEquals(producer.numberOfProducedMessages.intValue(), 0);
	}
}